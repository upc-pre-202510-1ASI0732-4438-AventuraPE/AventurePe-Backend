package com.upc.aventurape.platform.XPCard;

import com.upc.aventurape.platform.publication.domain.model.aggregates.Publication;
import com.upc.aventurape.platform.publication.domain.model.commands.DeleteCommentCommand;
import com.upc.aventurape.platform.publication.domain.model.entities.Comment;
import com.upc.aventurape.platform.publication.domain.services.PublicationCommandService;
import com.upc.aventurape.platform.publication.infrastructure.persistence.jpa.repositories.CommentRepository;
import com.upc.aventurape.platform.publication.infrastructure.persistence.jpa.repositories.PublicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TestCommentDeletionPerformance {

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PublicationCommandService publicationCommandService;

    @Test
    public void testCommentDeletionPerformance() {
        // Preparar datos
        Publication publication = publicationRepository.findAll().get(0);
        Comment comment = commentRepository.findByPublicationIdAndNotDeleted(publication.getId()).get(0);

        // Medir tiempo
        long startTime = System.currentTimeMillis();

        // Ejecutar eliminación
        publicationCommandService.handle(new DeleteCommentCommand(publication.getId(), comment.getId()));

        long endTime = System.currentTimeMillis();
        double seconds = (endTime - startTime) / 1000.0;

        System.out.println("Tiempo de eliminación: " + seconds + " segundos");
        assertTrue(seconds < 1.5, "La eliminación tardó más de 1.5 segundos");
    }
}