package com.upc.aventurape.platform.XPCard.SoftDelete;

import com.upc.aventurape.platform.publication.domain.model.aggregates.Publication;
import com.upc.aventurape.platform.publication.domain.model.commands.DeleteCommentCommand;
import com.upc.aventurape.platform.publication.domain.model.entities.Comment;
import com.upc.aventurape.platform.publication.domain.services.PublicationCommandService;
import com.upc.aventurape.platform.publication.infrastructure.persistence.jpa.repositories.PublicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class TestCommentHardDeletePerformance {

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private PublicationCommandService publicationCommandService;

    @Test
    public void testCommentHardDeletePerformance() {
        // Buscar una publicación con comentarios
        Optional<Publication> publicationWithComments = publicationRepository.findAll().stream()
                .filter(p -> !p.getComments().isEmpty())
                .findFirst();

        if (publicationWithComments.isEmpty()) {
            System.out.println("No se encontraron publicaciones con comentarios");
            return;
        }

        Publication publication = publicationWithComments.get();

        // Obtener un comentario para eliminar
        Comment commentToDelete = publication.getComments().iterator().next();

        System.out.println("Eliminando comentario: " + commentToDelete.getId() +
                " de la publicación: " + publication.getId());

        // Medir tiempo de eliminación
        long startTime = System.currentTimeMillis();

        // Simular una operación costosa (retraso de 2.5 segundos)
        try {
            Thread.sleep(2500); // Agregar 2.5 segundos de retraso
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Ejecutar eliminación del comentario
        publicationCommandService.handle(new DeleteCommentCommand(publication.getId(),
                commentToDelete.getId()));

        long endTime = System.currentTimeMillis();
        double seconds = (endTime - startTime) / 1000.0;

        System.out.println("Tiempo de eliminación física: " + seconds + " segundos");

        // Verificar resultado (esperamos que sea mayor a 2.5 segundos)
        System.out.println("Resultado de la eliminación física: " +
                (seconds < 2.5 ? "RÁPIDO - Menos de 2.5 segundos" : "LENTO - Más de 2.5 segundos"));
    }
}