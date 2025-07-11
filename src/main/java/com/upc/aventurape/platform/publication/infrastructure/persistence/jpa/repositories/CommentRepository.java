package com.upc.aventurape.platform.publication.infrastructure.persistence.jpa.repositories;

import com.upc.aventurape.platform.publication.domain.model.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.publication.id = :publicationId AND c.deleted = false")
    List<Comment> findByPublicationIdAndNotDeleted(@Param("publicationId") Long publicationId);

    @Query("SELECT c FROM Comment c WHERE c.deleted = false")
    List<Comment> findAllNotDeleted();

    Optional<Comment> findByIdAndPublicationId(Long id, Long publicationId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.publication.id = :publicationId AND c.deleted = false")
    Long countByPublicationIdAndNotDeleted(@Param("publicationId") Long publicationId);
}
