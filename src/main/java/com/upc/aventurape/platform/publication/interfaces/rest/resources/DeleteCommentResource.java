package com.upc.aventurape.platform.publication.interfaces.rest.resources;

public record DeleteCommentResource(
    Long publicationId,
    Long commentId
) {} 