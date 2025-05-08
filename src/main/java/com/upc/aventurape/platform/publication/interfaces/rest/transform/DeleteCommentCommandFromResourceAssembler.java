package com.upc.aventurape.platform.publication.interfaces.rest.transform;

import com.upc.aventurape.platform.publication.domain.model.commands.DeleteCommentCommand;
import com.upc.aventurape.platform.publication.interfaces.rest.resources.DeleteCommentResource;

public class DeleteCommentCommandFromResourceAssembler {
    public static DeleteCommentCommand toCommandFromResource(DeleteCommentResource resource) {
        return new DeleteCommentCommand(
            resource.publicationId(),
            resource.commentId()
        );
    }
} 