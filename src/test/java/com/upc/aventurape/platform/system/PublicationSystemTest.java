package com.upc.aventurape.platform.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.aventurape.platform.iam.domain.model.commands.SignInCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de sistema para la funcionalidad de publicaciones
 * Esta prueba verifica el flujo completo de creación, consulta y eliminación de publicaciones
 */
@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("unchecked") // Suprimimos advertencias de operaciones sin verificación
public class PublicationSystemTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private String authToken;
    private Long createdPublicationId;

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Autenticación para obtener token
        SignInCommand signInCommand = new SignInCommand(
                "entrepreneur",  // Usuario emprendedor predefinido
                "password123"
        );

        MvcResult result = mockMvc.perform(post("/api/v1/users/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInCommand)))
                .andReturn();

        Map<String, Object> responseMap = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                HashMap.class
        );
        authToken = (String) responseMap.get("token");
    }

    /**
     * US04: Creación y gestión de publicaciones de aventuras
     * Como emprendedor, quiero crear publicaciones de aventuras para promocionar mis servicios
     */
    @Test
    public void testPublicationLifecycle() throws Exception {
        // Paso 1: Crear una nueva publicación
        Map<String, Object> publicationData = new HashMap<>();
        publicationData.put("entrepreneurId", 1L);
        publicationData.put("nameActivity", "Aventura en los Andes");
        publicationData.put("description", "Una increíble aventura en las montañas");
        publicationData.put("cantPeople", 5);
        publicationData.put("timeDuration", 3);
        publicationData.put("cost", 500);
        publicationData.put("image", "https://example.com/image.jpg");

        MvcResult createResult = mockMvc.perform(post("/api/v1/publications")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(publicationData)))
                .andExpect(status().isCreated())
                .andReturn();

        // Uso de tipo genérico explícito para evitar advertencias
        Map<String, Object> createdPublication = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                HashMap.class
        );
        
        // Manejo seguro de la conversión de tipos
        Object idObject = createdPublication.get("id");
        if (idObject instanceof Number) {
            createdPublicationId = ((Number) idObject).longValue();
        } else if (idObject instanceof String) {
            createdPublicationId = Long.parseLong((String) idObject);
        } else {
            createdPublicationId = null;
        }
        
        assertNotNull(createdPublicationId, "El ID de la publicación creada no debe ser nulo");

        // Paso 2: Obtener la publicación creada por su ID
        mockMvc.perform(get("/api/v1/publications/{id}", createdPublicationId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdPublicationId))
                .andExpect(jsonPath("$.adventure.nameActivity").value("Aventura en los Andes"))
                .andExpect(jsonPath("$.cost").value(500));

        // Paso 3: Listar todas las publicaciones y verificar que incluya la recién creada
        mockMvc.perform(get("/api/v1/publications")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + createdPublicationId + ")]").exists());

        // Paso 4: Eliminar la publicación
        mockMvc.perform(delete("/api/v1/publications/{id}", createdPublicationId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        // Paso 5: Verificar que la publicación ya no existe
        mockMvc.perform(get("/api/v1/publications/{id}", createdPublicationId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }
} 