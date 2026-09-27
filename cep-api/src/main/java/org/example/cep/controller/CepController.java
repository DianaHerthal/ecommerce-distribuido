package org.example.cep.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cep")
public class CepController {

    private static final Logger log = LoggerFactory.getLogger("CEP-API");

    // Base fake de CEPs
    private final Map<String, Map<String, String>> baseCep = Map.of(
            "01310-100", Map.of("rua", "Av. Paulista",     "bairro", "Bela Vista", "cidade", "São Paulo",      "uf", "SP"),
            "20040-020", Map.of("rua", "Av. Rio Branco",   "bairro", "Centro",     "cidade", "Rio de Janeiro", "uf", "RJ"),
            "30130-010", Map.of("rua", "Av. Afonso Pena",  "bairro", "Centro",     "cidade", "Belo Horizonte", "uf", "MG")
    );

    @GetMapping("/{cep}")
    public ResponseEntity<Map<String, Object>> buscar(@PathVariable String cep) {
        log.info("REQUEST  GET /cep/{} - Consultando endereço", cep);

        Map<String, String> endereco = baseCep.get(cep);
        if (endereco == null) {
            log.warn("RESPONSE 404 - CEP {} não encontrado", cep);
            return ResponseEntity.status(404)
                    .body(Map.of("sucesso", false, "erro", "CEP não encontrado"));
        }

        log.info("RESPONSE 200 OK - Endereço encontrado: {} - {}, {}",
                endereco.get("rua"), endereco.get("cidade"), endereco.get("uf"));
        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "endereco", Map.of(
                        "cep", cep,
                        "rua", endereco.get("rua"),
                        "bairro", endereco.get("bairro"),
                        "cidade", endereco.get("cidade"),
                        "uf", endereco.get("uf")
                )
        ));
    }
}