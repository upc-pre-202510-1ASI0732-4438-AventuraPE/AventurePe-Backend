package com.upc.aventurape.platform.publication.domain.services;

import com.upc.aventurape.platform.publication.domain.model.entities.Favorite;
import com.upc.aventurape.platform.publication.domain.model.queries.GetAllFavoritePublicationsQuery;
import com.upc.aventurape.platform.publication.domain.model.queries.GetFavoritePublicationByProfileIdQuery;
import com.upc.aventurape.platform.publication.domain.model.queries.GetFavoritePublicationsByProfileIdOrderedByRatingQuery;

import java.util.List;

public interface FavoritePublicationQueryService {
    List<Favorite> handle(GetAllFavoritePublicationsQuery query);
    List<Favorite> handle(GetFavoritePublicationByProfileIdQuery query);

    Long getCountByPublicationId(Long publicationId);
}
