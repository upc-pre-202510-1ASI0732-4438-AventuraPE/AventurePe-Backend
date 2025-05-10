package com.upc.aventurape.platform.iam.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.aventurape.platform.iam.domain.model.aggregates.User;
import com.upc.aventurape.platform.iam.domain.model.commands.SignInCommand;
import com.upc.aventurape.platform.iam.domain.model.commands.SignUpCommand;
import com.upc.aventurape.platform.iam.domain.model.entities.Role;
import com.upc.aventurape.platform.iam.domain.model.queries.GetAllUsersQuery;
import com.upc.aventurape.platform.iam.domain.services.UserCommandService;
import com.upc.aventurape.platform.iam.domain.services.UserQueryService;
import com.upc.aventurape.platform.iam.domain.model.valueobjects.Roles;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.upc.aventurape.platform.iam.interfaces.rest.resources.SignUpResource;
import com.upc.aventurape.platform.iam.interfaces.rest.resources.SignInResource;
import com.upc.aventurape.platform.iam.domain.services.RecaptchaService;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserCommandService userCommandService;

    @MockBean
    private UserQueryService userQueryService;
    
    @MockBean
    private RecaptchaService recaptchaService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // Configurar que recaptcha siempre devuelva true en las pruebas
        when(recaptchaService.verifyRecaptcha(any())).thenReturn(true);
    }

    @Test
    public void testSignUp() throws Exception {
        // Arrange
        SignUpResource signUpResource = new SignUpResource(
                "testuser",
                "password123",
                "test@example.com",
                List.of("ROLE_ADVENTUROUS")
        );

        User mockUser = Mockito.mock(User.class);
        when(mockUser.getUsername()).thenReturn("testuser");
        when(mockUser.getEmail()).thenReturn("test@example.com");
        
        doReturn(Optional.of(mockUser)).when(userCommandService).handle(any(SignUpCommand.class));

        // Mockear también RecaptchaService para evitar validaciones
        // (En pruebas reales deberíamos verificar que esto funcione)
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/authentication/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("recaptchaToken", "test-token")
                        .content(objectMapper.writeValueAsString(signUpResource)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testSignIn() throws Exception {
        // Arrange
        SignInResource signInResource = new SignInResource(
                "testuser",
                "password123"
        );

        // Mock de respuesta de autenticación
        User mockUser = Mockito.mock(User.class);
        when(mockUser.getUsername()).thenReturn("testuser");
        when(mockUser.getEmail()).thenReturn("test@example.com");
        
        String token = "jwt-token";
        
        // Mockear el servicio para devolver un Optional<ImmutablePair<User, String>>
        ImmutablePair<User, String> authPair = new ImmutablePair<>(mockUser, token);
        doReturn(Optional.of(authPair)).when(userCommandService).handle(any(SignInCommand.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("recaptchaToken", "test-token")
                        .content(objectMapper.writeValueAsString(signInResource)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        // Arrange
        List<User> userList = new ArrayList<>();
        User user = Mockito.mock(User.class);
        userList.add(user);
        
        // Mockear el servicio utilizando doReturn para devolver un Optional<List>
        doReturn(Optional.of(userList)).when(userQueryService).handle(any(GetAllUsersQuery.class));

        // Act & Assert
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk());
    }
} 