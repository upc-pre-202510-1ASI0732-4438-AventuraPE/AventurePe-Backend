package com.upc.aventurape.platform.shared.interfaces.rest;

import com.upc.aventurape.platform.shared.domain.services.BarbaraNexusService;
import com.upc.aventurape.platform.shared.interfaces.rest.resources.BarbaraChatRequest;
import com.upc.aventurape.platform.shared.interfaces.rest.resources.BarbaraChatResponse;
import com.upc.aventurape.platform.shared.interfaces.rest.resources.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/barbara")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BarbaraNexusController {

    private final BarbaraNexusService barbaraNexusService;

    /**
     * Endpoint para enviar mensajes al chatbot Barbara Nexus
     */
    @PostMapping("/chat")
    public ResponseEntity<BarbaraChatResponse> sendMessage(@RequestBody BarbaraChatRequest request) {
        log.info("Recibida petición de chat para usuario: {}", request.userId());
        
        Optional<BarbaraNexusService.BarbaraResponse> response = 
            barbaraNexusService.sendMessage(request.message(), request.userId());
        
        if (response.isPresent()) {
            BarbaraNexusService.BarbaraResponse barbaraResponse = response.get();
            BarbaraChatResponse chatResponse = new BarbaraChatResponse(
                barbaraResponse.getResponse(),
                "success",
                LocalDateTime.now()
            );
            return ResponseEntity.ok(chatResponse);
        } else {
            BarbaraChatResponse errorResponse = new BarbaraChatResponse(
                "Lo siento, no pude procesar tu mensaje. Inténtalo de nuevo.",
                "error",
                LocalDateTime.now()
            );
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * Endpoint para verificar el estado del servicio Barbara Nexus
     */
    @GetMapping("/health")
    public ResponseEntity<MessageResource> checkHealth() {
        boolean isHealthy = barbaraNexusService.checkHealth();
        
        if (isHealthy) {
            return ResponseEntity.ok(new MessageResource("Barbara Nexus está funcionando correctamente"));
        } else {
            return ResponseEntity.status(503)
                .body(new MessageResource("Barbara Nexus no está disponible"));
        }
    }
} 