package org.example.loja.controller;

import org.example.loja.dto.PedidoRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comprar")
public class LojaController {

    private static final Logger log = LoggerFactory.getLogger("LOJA-WEB");

    private final RestTemplate rest;

    private static final String PRODUTOS_URL  = "http://localhost:8081";
    private static final String CEP_URL       = "http://localhost:8082";
    private static final String PAGAMENTO_URL = "http://localhost:8083";
    private static final String EMAIL_URL     = "http://localhost:8084";
    private static final String FISCAL_URL    = "http://localhost:8085";
    private static final String ENTREGA_URL   = "http://localhost:8086";

    public LojaController(RestTemplate rest) {
        this.rest = rest;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> comprar(@RequestBody PedidoRequest pedido) {
        log.info("════════ INÍCIO DO FLUXO DE COMPRA ════════");
        log.info("Pedido recebido: cliente={} itens={} cep={}",
                pedido.getNomeCliente(), pedido.getItens().size(), pedido.getCep());

        try {
            log.info("[Etapa 1] Solicitando catálogo de produtos");
            List<?> catalogo = rest.getForObject(PRODUTOS_URL + "/produtos", List.class);
            log.info("Catálogo recebido: {} produtos", catalogo != null ? catalogo.size() : 0);

            log.info("[Etapa 2] Produtos escolhidos pelo usuário");

            log.info("[Etapa 3] Dados de pagamento e endereço informados");

            log.info("[Etapa 4] Consultando CEP {}", pedido.getCep());
            Map<?, ?> cepResp = rest.getForObject(
                    CEP_URL + "/cep/" + pedido.getCep(), Map.class);
            if (cepResp == null || !Boolean.TRUE.equals(cepResp.get("sucesso"))) {
                throw new RuntimeException("CEP inválido");
            }
            Map<?, ?> endereco = (Map<?, ?>) cepResp.get("endereco");
            log.info("Endereço preenchido: {} - {} / {}",
                    endereco.get("rua"), endereco.get("cidade"), endereco.get("uf"));

            double valorTotal = pedido.getItens().stream()
                    .mapToDouble(i -> i.getPreco() * i.getQuantidade())
                    .sum();

            log.info("[Etapa 5] Compra CONFIRMADA valorTotal={}", valorTotal);

            log.info("[Etapa 6] Enviando e-mail de confirmação");
            enviarEmail(pedido.getEmailCliente(),
                    "Confirmação de Compra",
                    "Pedido no valor de R$ " + valorTotal);

            log.info("[Etapa 7] Processando pagamento");
            Map<String, Object> pagBody = Map.of(
                    "valor", valorTotal,
                    "metodo", pedido.getMetodoPagamento()
            );
            Map<?, ?> pagamento = rest.postForObject(
                    PAGAMENTO_URL + "/pagamentos", pagBody, Map.class);

            log.info("[Etapa 8] Enviando e-mail com resultado do pagamento");
            enviarEmail(pedido.getEmailCliente(),
                    "Pagamento " + (pagamento != null ? pagamento.get("status") : "?"),
                    "Transação: " + (pagamento != null ? pagamento.get("transacaoId") : "N/A"));

            if (pagamento == null || !Boolean.TRUE.equals(pagamento.get("sucesso"))) {
                throw new RuntimeException("Pagamento recusado");
            }

            log.info("[Etapa 9] Gerando NF e baixando estoque");

            Map<String, Object> nfBody = Map.of(
                    "cliente", pedido.getNomeCliente(),
                    "valorTotal", valorTotal,
                    "itens", pedido.getItens()
            );
            Map<?, ?> nfResp = rest.postForObject(
                    FISCAL_URL + "/notas-fiscais", nfBody, Map.class);

            Map<String, Object> baixaBody = Map.of("itens", pedido.getItens());
            rest.postForObject(
                    PRODUTOS_URL + "/produtos/baixa-estoque", baixaBody, Map.class);

            Map<?, ?> notaFiscal = (Map<?, ?>) nfResp.get("notaFiscal");
            log.info("[Etapa 10] Enviando e-mail com a nota fiscal");
            enviarEmail(pedido.getEmailCliente(),
                    "Nota Fiscal " + notaFiscal.get("numeroNF"),
                    "NF no valor de R$ " + valorTotal);

            log.info("[Etapa 11] Disponibilizando produtos para entrega");
            Map<String, Object> entregaBody = Map.of(
                    "endereco", endereco,
                    "itens", pedido.getItens()
            );
            Map<?, ?> entrega = rest.postForObject(
                    ENTREGA_URL + "/entregas", entregaBody, Map.class);

            log.info("[Etapa 12] Enviando e-mail com dados da entrega");
            enviarEmail(pedido.getEmailCliente(),
                    "Entrega agendada",
                    "Rastreio: " + entrega.get("codigoRastreio") +
                            " Previsão: " + entrega.get("previsao"));

            log.info("════════ FLUXO CONCLUÍDO COM SUCESSO ════════");

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("sucesso", true);
            resultado.put("transacaoId", pagamento.get("transacaoId"));
            resultado.put("notaFiscal", notaFiscal);
            resultado.put("entrega", entrega);
            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            log.error("Fluxo interrompido: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("sucesso", false, "erro", e.getMessage()));
        }
    }

    private void enviarEmail(String para, String assunto, String corpo) {
        Map<String, Object> body = Map.of(
                "para", para,
                "assunto", assunto,
                "corpo", corpo
        );
        rest.postForObject(EMAIL_URL + "/emails", body, Map.class);
    }
}
