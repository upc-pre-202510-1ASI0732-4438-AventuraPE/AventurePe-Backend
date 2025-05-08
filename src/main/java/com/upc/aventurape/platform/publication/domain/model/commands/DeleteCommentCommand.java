package com.upc.aventurape.platform.publication.domain.model.commands;

public record DeleteCommentCommand(
    Long publicationId,
    Long commentId
) {} 