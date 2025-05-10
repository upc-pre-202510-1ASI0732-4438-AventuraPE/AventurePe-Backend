package com.upc.aventurape.platform.publication.domain.services;

import com.upc.aventurape.platform.publication.domain.model.aggregates.Publication;
import com.upc.aventurape.platform.publication.domain.model.commands.*;
import com.upc.aventurape.platform.publication.domain.model.entities.Comment;
import com.upc.aventurape.platform.publication.domain.model.entities.Favorite;

import java.util.Optional;

public interface PublicationCommandService {
    Publication handle(CreatePublicationCommand command);
    Optional<Publication> handle(UpdatePublicationCommand command);
    void handle(DeletePublicationCommand command);
    Comment handle(AddCommentToPublicationCommand command);
    void handle(AssignEntrepreneurToPublicationCommand command);
    void handle(DeleteCommentCommand command);
}
