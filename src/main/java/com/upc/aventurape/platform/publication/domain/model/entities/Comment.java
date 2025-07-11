package com.upc.aventurape.platform.publication.domain.model.entities;

import com.upc.aventurape.platform.iam.infrastructure.security.SecurityUtils;
import com.upc.aventurape.platform.publication.domain.model.aggregates.Publication;
import com.upc.aventurape.platform.publication.domain.model.valueobjects.CommentManager;
import com.upc.aventurape.platform.publication.domain.model.valueobjects.ProfileId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "comments", indexes = {
        @Index(name = "idx_comments_publication_deleted", columnList = "publication_id, deleted"),
        @Index(name = "idx_comments_deleted", columnList = "deleted")
})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    // ESTO ESTA BIEN?
    @JoinColumn(name = "publication_id", nullable = false)
    private Publication publication;

    @Embedded
    private CommentManager commentManager;

    private String content;

    private Short rating;

    @Embedded
    private ProfileId profileid;

    private Long adventureId;

    // EXP: SOFT DELETE
    @Setter
    private boolean deleted = false;

    @Setter
    private LocalDateTime deletedAt;

    // Metodo para marcar como eliminado
    public void markAsDeleted() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    // Default constructor
    public Comment(){
        this.publication = new Publication();
        this.content = "";
        this.rating = 0;
        this.profileid = new ProfileId();
        this.adventureId = SecurityUtils.getCurrentUserId();
    }

    public Comment(Publication publication, String content, Short rating) {
        this.publication = publication;
        this.content = content;
        this.rating = rating;
        this.adventureId = SecurityUtils.getCurrentUserId();
    }

    // Implement equals and hashCode based on ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id != null && id.equals(comment.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}