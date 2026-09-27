package org.example.entrega.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/entregas")
public class EntregaController {

    private static final Logger log = LoggerFactory.getLogger("ENTREGA-API");

    @PostMapping
    public ResponseEntity<Map<String, Object>> agendar(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        Map<String, Object> endereco = (Map<String, Object>) body.get("endereco");
        @SuppressWarnings("unchecked")
        List<Object> itens = (List<Object>) body.get("itens");

        log.info("REQUEST  POST /entregas → cidade={} itens={}",
                endereco.get("cidade"), itens.size());

        String codigoRastreio = "BR-" + System.currentTimeMillis();
        String previsao = LocalDate.now().plusDays(5).toString();

        log.info("RESPONSE 201 Created - Entrega agendada rastreio={} previsao={}",
                codigoRastreio, previsao);

        return ResponseEntity.status(201).body(Map.of(
                "sucesso", true,
                "codigoRastreio", codigoRastreio,
                "previsao", previsao
        ));
    }
}
