package com.upc.aventurape.platform.publication.application.internal.queryservices;

import com.upc.aventurape.platform.publication.domain.model.aggregates.Publication;
import com.upc.aventurape.platform.publication.domain.model.entities.Adventure;
import com.upc.aventurape.platform.publication.domain.model.entities.Comment;
import com.upc.aventurape.platform.publication.domain.model.entities.Favorite;
import com.upc.aventurape.platform.publication.domain.model.queries.*;
import com.upc.aventurape.platform.publication.domain.services.PublicationQueryService;
import com.upc.aventurape.platform.publication.infrastructure.persistence.jpa.repositories.PublicationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.upc.aventurape.platform.publication.infrastructure.persistence.jpa.repositories.CommentRepository;


@Service
public class PublicationQueryServiceImpl implements PublicationQueryService {

    private final PublicationRepository publicationRepository;
    // EXP Soft delete: implementar repositorio de comentarios
    private final CommentRepository commentRepository;

    public PublicationQueryServiceImpl(PublicationRepository publicationRepository,
                                       CommentRepository commentRepository) {
        this.publicationRepository = publicationRepository;
        this.commentRepository = commentRepository;
    }


    @Override
    public Optional<Publication> handle(GetPublicationByIdQuery query) {
        return publicationRepository.findById(query.publicationId());
    }

    @Override
    public List<Publication> handle(GetAllPublicationsQuery query) {
        return publicationRepository.findAll();
    }

    @Override
    public List<Publication> handle(GetPublicationByEntrepeneurIdQuery query) {
        return publicationRepository.findByEntrepreneurId(query.entrepreneurId());
    }

    @Override
    public List<Comment> handle(GetAllCommentsQuery query) {
        return commentRepository.findAllNotDeleted();
    }

    @Override
    public Optional<List<Comment>> handle(GetCommentsByPublicationIdQuery query) {
        // Usar el método que filtra comentarios eliminados
        List<Comment> activeComments = commentRepository.findByPublicationIdAndNotDeleted(query.publicationId());
        return Optional.of(activeComments);
    }

    @Override
    public Optional<Adventure> handle(GetAdventureByPublicationIdQuery query) {
        return publicationRepository.findById(query.publicationId()).map(Publication::getAdventure);
    }

    @Override
    public List<Publication> handle(GetFavoritePublicationsByProfileIdOrderedByRatingQuery query) {
        return publicationRepository.findByEntrepreneurId(query.entrepreneurId()).stream()
                .sorted(Comparator.comparingDouble(Publication::getAverageRating).reversed())
                .collect(Collectors.toList());
    }
    @Override
    public Long getCommentsCountByPublicationId(Long publicationId) {
        // Usar el metodo que cuenta solo comentarios no eliminados
        return commentRepository.countByPublicationIdAndNotDeleted(publicationId);
    }
}
