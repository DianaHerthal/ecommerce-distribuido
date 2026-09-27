package org.example.pagamento.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private static final Logger log = LoggerFactory.getLogger("PAGAMENTO-API");

    @PostMapping
    public ResponseEntity<Map<String, Object>> processar(@RequestBody Map<String, Object> body) {
        Double valor = ((Number) body.get("valor")).doubleValue();
        String metodo = (String) body.getOrDefault("metodo", "CREDITO");

        log.info("REQUEST  POST /pagamentos - valor={} metodo={}", valor, metodo);

        boolean aprovado = ThreadLocalRandom.current().nextDouble() > 0.1;

        if (!aprovado) {
            log.warn("RESPONSE 402 - Transação recusada");
            return ResponseEntity.status(402).body(Map.of(
                    "sucesso", false,
                    "status", "RECUSADO",
                    "motivo", "Saldo insuficiente"
            ));
        }

        String transacaoId = "TX-" + System.currentTimeMillis();
        log.info("RESPONSE 200 OK - Aprovado transacaoId={}", transacaoId);
        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "status", "APROVADO",
                "transacaoId", transacaoId
        ));
    }
}