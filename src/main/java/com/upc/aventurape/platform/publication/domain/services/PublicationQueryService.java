package com.upc.aventurape.platform.publication.domain.services;

import com.upc.aventurape.platform.publication.domain.model.aggregates.Publication;
import com.upc.aventurape.platform.publication.domain.model.entities.Adventure;
import com.upc.aventurape.platform.publication.domain.model.entities.Comment;
import com.upc.aventurape.platform.publication.domain.model.entities.Favorite;
import com.upc.aventurape.platform.publication.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

public interface PublicationQueryService {

    Optional<Publication> handle(GetPublicationByIdQuery query);
    List<Publication> handle(GetAllPublicationsQuery query);
    List<Publication> handle (GetPublicationByEntrepeneurIdQuery query);
    List<Comment> handle(GetAllCommentsQuery query);
    Optional<List<Comment>> handle(GetCommentsByPublicationIdQuery query);
    Optional<Adventure> handle(GetAdventureByPublicationIdQuery query);
    // New method to handle the query
    List<Publication> handle(GetFavoritePublicationsByProfileIdOrderedByRatingQuery query);
    Long getCommentsCountByPublicationId(Long publicationId);

}
