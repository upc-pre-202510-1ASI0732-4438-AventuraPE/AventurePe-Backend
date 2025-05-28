// src/main/java/com/upc/aventurape/platform/publication/infrastructure/persistence/jpa/repositories/FavoritePublicationRepository.java
package com.upc.aventurape.platform.publication.infrastructure.persistence.jpa.repositories;

import com.upc.aventurape.platform.publication.domain.model.entities.Favorite;
import com.upc.aventurape.platform.publication.domain.model.valueobjects.ProfileId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritePublicationRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByProfileId(ProfileId profileId);
    Long countByPublicationId(Long publicationId);
}