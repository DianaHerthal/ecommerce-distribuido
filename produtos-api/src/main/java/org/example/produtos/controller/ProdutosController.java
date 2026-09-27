package org.example.produtos.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/produtos")
public class ProdutosController {

    private static final Logger log = LoggerFactory.getLogger("PRODUTOS-API");

    private final List<org.example.produtos.model.Produto> catalogo = new ArrayList<>(List.of(
            new org.example.produtos.model.Produto(1L, "Notebook Dell",    4500.00, 10),
            new org.example.produtos.model.Produto(2L, "Mouse Logitech",   150.00,  50),
            new org.example.produtos.model.Produto(3L, "Teclado Mecânico", 350.00,  30)
    ));

    @GetMapping
    public List<org.example.produtos.model.Produto> listar() {
        log.info("REQUEST  GET /produtos - Listando catálogo");
        log.info("RESPONSE 200 OK - {} produtos retornados", catalogo.size());
        return catalogo;
    }

    @PostMapping("/baixa-estoque")
    public Map<String, Object> baixarEstoque(@RequestBody Map<String, List<Map<String, Object>>> body) {
        List<Map<String, Object>> itens = body.get("itens");
        log.info("REQUEST  POST /produtos/baixa-estoque - {} itens", itens.size());

        for (Map<String, Object> item : itens) {
            Long id = ((Number) item.get("id")).longValue();
            Integer qtd = ((Number) item.get("quantidade")).intValue();
            catalogo.stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .ifPresent(p -> {
                        p.setEstoque(p.getEstoque() - qtd);
                        log.info("INFO     Estoque atualizado: {} → {} un.", p.getNome(), p.getEstoque());
                    });
        }
        log.info("RESPONSE 200 OK - Estoque baixado");
        return Map.of("sucesso", true);
    }
}