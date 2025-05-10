package com.upc.aventurape.platform.profiles.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.aventurape.platform.profiles.domain.model.aggregates.Profile;
import com.upc.aventurape.platform.profiles.domain.model.aggregates.ProfileAdventurer;
import com.upc.aventurape.platform.profiles.domain.model.commands.CreateProfileAdventurerCommand;
import com.upc.aventurape.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.upc.aventurape.platform.profiles.domain.model.commands.CreateProfileEntrepreneurCommand;
import com.upc.aventurape.platform.profiles.domain.model.queries.GetProfileEntrepreneurByIdQuery;
import com.upc.aventurape.platform.profiles.domain.services.ProfileCommandService;
import com.upc.aventurape.platform.profiles.domain.services.ProfileQueryService;
import com.upc.aventurape.platform.iam.infrastructure.security.SecurityUtils;
import com.upc.aventurape.platform.profiles.interfaces.rest.resources.CreateProfileAdventurerResource;
import com.upc.aventurape.platform.profiles.domain.services.ProfileAdventureCommandService;
import com.upc.aventurape.platform.profiles.domain.model.aggregates.ProfileEntrepreneur;
import com.upc.aventurape.platform.profiles.interfaces.rest.resources.CreateProfileEntrepreneurResource;
import com.upc.aventurape.platform.profiles.domain.services.ProfileEntrepreneurCommandService;
import com.upc.aventurape.platform.profiles.domain.model.valueobjects.UserId;
import com.upc.aventurape.platform.profiles.domain.services.ProfileEntrepreneurQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class ProfileControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileCommandService profileCommandService;

    @MockBean
    private ProfileQueryService profileQueryService;

    @MockBean
    private ProfileAdventureCommandService profileAdventureCommandService;

    @MockBean
    private ProfileEntrepreneurCommandService profileEntrepreneurCommandService;

    @MockBean
    private ProfileEntrepreneurQueryService profileEntrepreneurQueryService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testCreateProfileAdventurer() throws Exception {
        // Arrange
        CreateProfileAdventurerResource resource = new CreateProfileAdventurerResource(
                "John",
                "Doe",
                "john.doe@example.com",
                "Main St",
                "123",
                "New York",
                "10001",
                "USA",
                "MALE"
        );

        Long userId = 1L;

        // Mock the SecurityUtils.getCurrentUserId() to return a non-null value
        try (MockedStatic<SecurityUtils> mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(userId);

            // Crear un ProfileAdventurer completamente inicializado
            ProfileAdventurer profile = new ProfileAdventurer(
                new UserId(userId),
                "John",
                "Doe",
                "john.doe@example.com",
                "Main St",
                "123",
                "New York",
                "10001",
                "USA",
                "MALE"
            );
            profile.setId(1L);
            
            // Mock the service to return a valid profile
            doReturn(profile).when(profileAdventureCommandService).handle(any(CreateProfileAdventurerCommand.class));

            // Act & Assert
            mockMvc.perform(post("/api/v1/profiles/adventurer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(resource)))
                    .andExpect(status().isCreated());
        }
    }

    @Test
    public void testCreateProfileEntrepreneur() throws Exception {
        // Arrange
        CreateProfileEntrepreneurResource resource = new CreateProfileEntrepreneurResource(
                "Adventure Tours",
                "Los Angeles",
                "USA",
                "456",
                "90001",
                "Oak St",
                "jane.smith@example.com"
        );

        Long userId = 1L;

        // Mock the SecurityUtils.getCurrentUserId() to return a non-null value
        try (MockedStatic<SecurityUtils> mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(userId);

            // Crear un ProfileEntrepreneur completamente inicializado
            ProfileEntrepreneur profile = new ProfileEntrepreneur(
                new UserId(userId),
                "jane.smith@example.com",
                "Oak St",
                "456",
                "Los Angeles",
                "90001",
                "USA",
                "Adventure Tours"
            );
            profile.setId(1L);
            
            // Mock the service to return a valid profile
            doReturn(Optional.of(profile)).when(profileEntrepreneurCommandService).handle(any(CreateProfileEntrepreneurCommand.class));

            // Act & Assert
            mockMvc.perform(post("/api/v1/profiles/entrepreneur")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(resource)))
                    .andExpect(status().isCreated());
        }
    }

    @Test
    public void testGetProfileById() throws Exception {
        // Arrange
        Long profileId = 1L;
        Long userId = 1L;
        
        // Crear un ProfileEntrepreneur completamente inicializado
        ProfileEntrepreneur profile = new ProfileEntrepreneur(
            new UserId(userId),
            "jane.smith@example.com",
            "Oak St",
            "456",
            "Los Angeles",
            "90001",
            "USA",
            "Adventure Tours"
        );
        profile.setId(profileId);
        
        Optional<ProfileEntrepreneur> optionalProfile = Optional.of(profile);

        // Mock the service to return the profile
        doReturn(optionalProfile).when(profileEntrepreneurQueryService).handle(any(GetProfileEntrepreneurByIdQuery.class));

        // Act & Assert
        mockMvc.perform(get("/api/v1/profiles/entrepreneur/{profileId}", profileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profileId));
    }
} 