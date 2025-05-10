package com.upc.aventurape.platform.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.aventurape.platform.iam.domain.model.commands.SignInCommand;
import com.upc.aventurape.platform.iam.domain.model.commands.SignUpCommand;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de sistema para la funcionalidad de usuarios
 * Esta prueba verifica el flujo completo de registro, inicio de sesión y obtención de perfil de usuario
 */
@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("unchecked") // Suprimimos advertencias de operaciones sin verificación
public class UserSystemTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private String authToken;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /**
     * US01: Registro de usuario
     * Como usuario nuevo, quiero poder registrarme en la plataforma para acceder a los servicios
     */
    @Test
    public void testUserRegistrationAndAuthentication() throws Exception {
        // Paso 1: Registrar un nuevo usuario
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        SignUpCommand signUpCommand = new SignUpCommand(
                "testuser_" + uniqueSuffix,
                "test_" + uniqueSuffix + "@example.com",
                "password123",
                null
        );

        mockMvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        // Paso 2: Iniciar sesión con el usuario registrado
        SignInCommand signInCommand = new SignInCommand(
                signUpCommand.username(),
                signUpCommand.password()
        );

        MvcResult result = mockMvc.perform(post("/api/v1/users/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value(signUpCommand.username()))
                .andReturn();

        // Extraer el token de autenticación para usarlo en futuras solicitudes
        // Uso de tipo genérico explícito para evitar advertencias
        Map<String, Object> responseMap = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                HashMap.class
        );
        
        // Manejo seguro de la conversión de tipos
        Object tokenObject = responseMap.get("token");
        if (tokenObject instanceof String) {
            authToken = (String) tokenObject;
        } else {
            authToken = null;
        }
        
        assertNotNull(authToken, "El token de autenticación no debe ser nulo");

        // Paso 3: Acceder a un recurso protegido usando el token
        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(signUpCommand.username()));
    }
} 