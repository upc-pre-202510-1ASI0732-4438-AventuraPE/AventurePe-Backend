package com.upc.aventurape.platform.shared.domain.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class BarbaraNexusService {

    @Value("${barbara.nexus.base-url:https://barbara-nexus.onrender.com}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public BarbaraNexusService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Envía un mensaje al chatbot Barbara Nexus
     * @param message Mensaje del usuario
     * @param userId ID del usuario
     * @return Respuesta del chatbot
     */
    public Optional<BarbaraResponse> sendMessage(String message, String userId) {
        try {
            BarbaraRequest request = new BarbaraRequest(message, userId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<BarbaraRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<BarbaraResponse> response = restTemplate.exchange(
                baseUrl + "/chat",
                HttpMethod.POST,
                entity,
                BarbaraResponse.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Respuesta exitosa de Barbara Nexus para usuario: {}", userId);
                return Optional.of(response.getBody());
            }
            
        } catch (HttpClientErrorException e) {
            log.error("Error HTTP al comunicarse con Barbara Nexus: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al comunicarse con Barbara Nexus: {}", e.getMessage());
        }
        
        return Optional.empty();
    }

    /**
     * Verifica el estado del servicio Barbara Nexus
     * @return true si el servicio está disponible
     */
    public boolean checkHealth() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/health", String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.error("Error al verificar salud de Barbara Nexus: {}", e.getMessage());
            return false;
        }
    }

    @Data
    public static class BarbaraRequest {
        @JsonProperty("message")
        private String message;
        
        @JsonProperty("user_id")
        private String userId;

        public BarbaraRequest(String message, String userId) {
            this.message = message;
            this.userId = userId;
        }
    }

    @Data
    public static class BarbaraResponse {
        @JsonProperty("response")
        private String response;
        
        @JsonProperty("status")
        private String status;
        
        @JsonProperty("timestamp")
        private LocalDateTime timestamp;
    }
} 