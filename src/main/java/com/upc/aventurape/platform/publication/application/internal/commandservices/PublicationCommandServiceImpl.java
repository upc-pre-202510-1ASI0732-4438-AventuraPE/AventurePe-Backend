package com.upc.aventurape.platform.publication.application.internal.commandservices;

import com.upc.aventurape.platform.iam.infrastructure.security.SecurityUtils;
import com.upc.aventurape.platform.publication.domain.model.aggregates.Publication;
import com.upc.aventurape.platform.publication.domain.model.commands.*;
import com.upc.aventurape.platform.publication.domain.model.entities.Adventure;
import com.upc.aventurape.platform.publication.domain.model.entities.Comment;
import com.upc.aventurape.platform.publication.domain.model.valueobjects.EntrepreneurId;
import com.upc.aventurape.platform.publication.domain.services.PublicationCommandService;
import com.upc.aventurape.platform.publication.infrastructure.persistence.jpa.repositories.PublicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.Optional;
@Service
public class PublicationCommandServiceImpl implements PublicationCommandService {

    private final PublicationRepository publicationRepository;

    // Definir el logger como constante de clase
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicationCommandServiceImpl.class);
    public PublicationCommandServiceImpl(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    @Override
    public Publication handle(CreatePublicationCommand command) {
        var adventure = new Adventure(command.nameActivity(), command.description(), command.cantPeople(), command.timeDuration());
        var publication = new Publication(new EntrepreneurId(command.entrepreneurId()), adventure, command.cost(), command.image());
        publicationRepository.save(publication);
        return publication;
    }

    @Override
    public Optional<Publication> handle(UpdatePublicationCommand command) {
        LOGGER.debug("Iniciando actualización de publicación con ID: {}", command.publicationId());

        if (!publicationRepository.existsById(command.publicationId())) {
            LOGGER.error("No se encontró la publicación con ID: {}", command.publicationId());
            throw new IllegalArgumentException("Publication does not exist");
        }

        return publicationRepository.findById(command.publicationId())
                .map(publication -> {
                    LOGGER.debug("Publicación encontrada: ID={}, EntrepreneurId={}",
                            publication.getId(),
                            publication.getEntrepreneurId());

                    // Actualizar adventure existente en lugar de crear uno nuevo
                    Adventure existingAdventure = publication.getAdventure();
                    if (existingAdventure == null) {
                        LOGGER.debug("Creando nueva Adventure para la publicación");
                        existingAdventure = new Adventure();
                        existingAdventure.setPublication(publication);
                        publication.setAdventure(existingAdventure);
                    }

                    // Actualizar los campos de Adventure
                    existingAdventure.setNameActivity(command.nameActivity());
                    existingAdventure.setDescription(command.description());
                    existingAdventure.setCantPeople(command.cantPeople());
                    existingAdventure.setTimeDuration(command.timeDuration());

                    LOGGER.debug("Adventure actualizada: nameActivity={}, description={}",
                            command.nameActivity(),
                            command.description());

                    // Actualizar campos de Publication
                    publication.setCost(command.cost());
                    publication.setEntrepreneurId(new EntrepreneurId(command.entrepreneurId()));
                    publication.setImage(command.image());

                    LOGGER.debug("Campos de Publication actualizados: cost={}, entrepreneurId={}",
                            command.cost(),
                            command.entrepreneurId());

                    // Guardar la publicación actualizada
                    Publication savedPublication = publicationRepository.save(publication);
                    LOGGER.debug("Publicación guardada exitosamente: ID={}", savedPublication.getId());

                    return savedPublication;
                });
    }

    @Override
    public void handle(DeletePublicationCommand command) {
        if (!publicationRepository.existsById(command.publicationId())) {
            throw new IllegalArgumentException("Publication does not exist");
        }
        publicationRepository.deleteById(command.publicationId());
    }

    @Override
    public Comment handle(AddCommentToPublicationCommand command) {
        if (!publicationRepository.existsById(command.publicationId())) {
            throw new IllegalArgumentException("Publication does not exist");
        }
        var publication = publicationRepository.findById(command.publicationId()).get();
        var comment = new Comment(publication, command.content(), command.rating());
        publication.getComments().add(comment);
        publicationRepository.save(publication);
        return comment;
    }

    @Override
    public void handle(AssignEntrepreneurToPublicationCommand command) {
        if (!publicationRepository.existsById(command.publicationId())) {
            throw new IllegalArgumentException("Publication does not exist");
        }
        var publication = publicationRepository.findById(command.publicationId()).get();
        publication.updateEntrepreneurId(command.entrepreneurId());
        publicationRepository.save(publication);
    }

    @Override
    @Transactional
    public void handle(DeleteCommentCommand command) {
        var publication = publicationRepository.findById(command.publicationId())
                .orElseThrow(() -> new RuntimeException("Publication not found"));

        var comment = publication.getComments().stream()
                .filter(c -> c.getId().equals(command.commentId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Verificar que el usuario que elimina es el dueño del comentario
        if (!comment.getAdventureId().equals(SecurityUtils.getCurrentUserId())) {
            throw new RuntimeException("You can only delete your own comments");
        }

        publication.getComments().remove(comment);
        publicationRepository.save(publication);
    }
}