package org.example.fiscal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notas-fiscais")
public class FiscalController {

    private static final Logger log = LoggerFactory.getLogger("FISCAL-API");

    @PostMapping
    public ResponseEntity<Map<String, Object>> gerar(@RequestBody Map<String, Object> body) {
        String cliente = (String) body.get("cliente");
        Double valorTotal = ((Number) body.get("valorTotal")).doubleValue();

        log.info("REQUEST  POST /notas-fiscais → cliente={} valor={}", cliente, valorTotal);

        String numeroNF = "NF-" + System.currentTimeMillis();

        Map<String, Object> notaFiscal = new HashMap<>();
        notaFiscal.put("numeroNF", numeroNF);
        notaFiscal.put("emitidaEm", Instant.now().toString());
        notaFiscal.put("valorTotal", valorTotal);
        notaFiscal.put("itens", body.get("itens"));

        log.info("RESPONSE 201 Created - NF gerada numeroNF={}", numeroNF);
        return ResponseEntity.status(201).body(Map.of(
                "sucesso", true,
                "notaFiscal", notaFiscal
        ));
    }
}
