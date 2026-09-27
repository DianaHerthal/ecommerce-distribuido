package org.example.email.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/emails")
public class EmailController {

    private static final Logger log = LoggerFactory.getLogger("EMAIL-API");

    @PostMapping
    public Map<String, Object> enviar(@RequestBody Map<String, String> body) {
        String para = body.get("para");
        String assunto = body.get("assunto");

        log.info("REQUEST  POST /emails → para={} assunto=\"{}\"", para, assunto);
        String messageId = "MSG-" + System.currentTimeMillis();
        log.info("RESPONSE 200 OK - E-mail enviado messageId={}", messageId);

        return Map.of("sucesso", true, "messageId", messageId);
    }
}
