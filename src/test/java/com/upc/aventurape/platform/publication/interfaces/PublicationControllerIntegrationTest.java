package com.upc.aventurape.platform.publication.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.aventurape.platform.iam.infrastructure.security.SecurityUtils;
import com.upc.aventurape.platform.publication.domain.model.aggregates.Publication;
import com.upc.aventurape.platform.publication.domain.model.commands.CreatePublicationCommand;
import com.upc.aventurape.platform.publication.domain.model.commands.DeletePublicationCommand;
import com.upc.aventurape.platform.publication.domain.model.entities.Adventure;
import com.upc.aventurape.platform.publication.domain.model.queries.GetAllPublicationsQuery;
import com.upc.aventurape.platform.publication.domain.model.queries.GetPublicationByIdQuery;
import com.upc.aventurape.platform.publication.domain.model.valueobjects.EntrepreneurId;
import com.upc.aventurape.platform.publication.domain.services.PublicationCommandService;
import com.upc.aventurape.platform.publication.domain.services.PublicationQueryService;
import com.upc.aventurape.platform.publication.interfaces.rest.PublicationController;
import com.upc.aventurape.platform.publication.interfaces.rest.resources.CreatePublicationResource;
import com.upc.aventurape.platform.publication.interfaces.rest.resources.PublicationResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PublicationControllerIntegrationTest {

    @Mock
    private PublicationCommandService publicationCommandService;

    @Mock
    private PublicationQueryService publicationQueryService;

    @InjectMocks
    private PublicationController publicationController;

    @Test
    public void testCreatePublication() {
        // Arrange
        Long entrepreneurId = 1L;
        CreatePublicationResource resource = new CreatePublicationResource(
                "Aventura en los Andes",
                "Una increíble aventura en las montañas",
                3,
                "https://example.com/image.jpg",
                5,
                500
        );
        
        Publication publication = createCompletePublication(1L);
        
        // Mock SecurityUtils.getCurrentUserId() para que retorne el ID del emprendedor
        try (MockedStatic<SecurityUtils> mockedStatic = Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(entrepreneurId);
            
            // Mock del servicio
            when(publicationCommandService.handle(any(CreatePublicationCommand.class))).thenReturn(publication);
            
            // Act
            ResponseEntity<PublicationResource> response = publicationController.createPublication(resource);
            
            // Assert
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1L, response.getBody().Id());
        }
    }

    @Test
    public void testGetAllPublications() {
        // Arrange
        List<Publication> publications = new ArrayList<>();
        Publication publication1 = createCompletePublication(1L);
        Publication publication2 = createCompletePublication(2L);
        publications.add(publication1);
        publications.add(publication2);

        when(publicationQueryService.handle(any(GetAllPublicationsQuery.class))).thenReturn(publications);

        // Act
        ResponseEntity<List<PublicationResource>> response = publicationController.getAllPublications();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void testGetPublicationById() {
        // Arrange
        Long publicationId = 1L;
        Publication publication = createCompletePublication(publicationId);
        Optional<Publication> optionalPublication = Optional.of(publication);

        when(publicationQueryService.handle(any(GetPublicationByIdQuery.class))).thenReturn(optionalPublication);

        // Act
        ResponseEntity<PublicationResource> response = publicationController.getPublicationById(publicationId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(publicationId, response.getBody().Id());
    }

    @Test
    public void testDeletePublication() {
        // Arrange
        Long publicationId = 1L;
        
        // Act
        ResponseEntity<Void> response = publicationController.deletePublication(publicationId);
        
        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    private Publication createCompletePublication(Long id) {
        // Crear una instancia completa de Publication con todas las propiedades necesarias
        EntrepreneurId entrepreneurId = new EntrepreneurId(1L);
        Adventure adventure = new Adventure();
        adventure.setNameActivity("Aventura en los Andes");
        adventure.setDescription("Una increíble aventura en las montañas");
        adventure.setCantPeople(5);
        adventure.setTimeDuration(3);
        
        Publication publication = new Publication(entrepreneurId, adventure, 500, "https://example.com/image.jpg");
        publication.setId(id);
        adventure.setPublication(publication);
        
        return publication;
    }
} 