package com.upc.aventurape.platform.publication.domain.model;

import com.upc.aventurape.platform.publication.domain.model.aggregates.Publication;
import com.upc.aventurape.platform.publication.domain.model.entities.Adventure;
import com.upc.aventurape.platform.publication.domain.model.entities.Comment;
import com.upc.aventurape.platform.publication.domain.model.valueobjects.EntrepreneurId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PublicationTest {

    @Test
    public void testPublicationCreation() {
        // Arrange
        Long entrepreneurId = 1L;
        EntrepreneurId entId = new EntrepreneurId(entrepreneurId);
        Adventure adventure = new Adventure();
        adventure.setNameActivity("Aventura en los Andes");
        adventure.setDescription("Una increíble aventura en las montañas");
        adventure.setTimeDuration(3);
        adventure.setCantPeople(5);
        Integer cost = 500;
        String image = "https://example.com/image.jpg";

        // Act
        Publication publication = new Publication(entId, adventure, cost, image);

        // Assert
        assertEquals(entrepreneurId, publication.getEntrepreneurId().entrepreneurId());
        assertEquals("Aventura en los Andes", publication.getAdventure().getNameActivity());
        assertEquals("Una increíble aventura en las montañas", publication.getAdventure().getDescription());
        assertEquals(3, publication.getAdventure().getTimeDuration());
        assertEquals(5, publication.getAdventure().getCantPeople());
        assertEquals(cost, publication.getCost());
        assertEquals(image, publication.getImage());
        assertEquals(0.0, publication.getRating());
    }

    @Test
    public void testUpdateCost() {
        // Arrange
        Publication publication = new Publication();
        Integer newCost = 750;

        // Act
        publication.updateCost(newCost);

        // Assert
        assertEquals(newCost, publication.getCost());
    }

    @Test
    public void testUpdateEntrepreneurId() {
        // Arrange
        Publication publication = new Publication();
        Long entrepreneurId = 2L;
        EntrepreneurId entId = new EntrepreneurId(entrepreneurId);

        // Act
        publication.updateEntrepreneurId(entId);

        // Assert
        assertEquals(entrepreneurId, publication.getEntrepreneurId().entrepreneurId());
    }

    @Test
    public void testCalculateAverageRating() {
        // Arrange
        Publication publication = new Publication();

        // Create comments with ratings
        Comment comment1WithRating = new Comment(publication, "Buen servicio", (short)4);
        Comment comment2WithRating = new Comment(publication, "Excelente experiencia", (short)5);
        Comment comment3WithRating = new Comment(publication, "Podría mejorar", (short)3);

        // Add comments using the addComment helper method
        publication.addComment(comment1WithRating);
        publication.addComment(comment2WithRating);
        publication.addComment(comment3WithRating);

        // Act
        double averageRating = publication.getAverageRating();

        // Assert
        assertEquals(4.0, averageRating);
    }
}