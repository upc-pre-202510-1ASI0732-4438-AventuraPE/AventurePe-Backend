// src/main/java/com/upc/aventurape/platform/publication/interfaces/rest/PublicationController.java
package com.upc.aventurape.platform.publication.interfaces.rest;

import com.upc.aventurape.platform.iam.infrastructure.security.SecurityUtils;
import com.upc.aventurape.platform.publication.domain.model.aggregates.Publication;
import com.upc.aventurape.platform.publication.domain.model.commands.DeletePublicationCommand;
import com.upc.aventurape.platform.publication.domain.model.commands.DeleteCommentCommand;
import com.upc.aventurape.platform.publication.domain.model.queries.*;
import com.upc.aventurape.platform.publication.domain.services.PublicationCommandService;
import com.upc.aventurape.platform.publication.domain.services.PublicationQueryService;
import com.upc.aventurape.platform.publication.interfaces.rest.resources.*;
import com.upc.aventurape.platform.publication.interfaces.rest.transform.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/publication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Publications")
public class PublicationController {
    private final PublicationCommandService publicationCommandService;
    private final PublicationQueryService publicationQueryService;

    public PublicationController(PublicationCommandService publicationCommandService, PublicationQueryService publicationQueryService) {
        this.publicationCommandService = publicationCommandService;
        this.publicationQueryService = publicationQueryService;
    }



    @PostMapping("/create-publication")
    @PreAuthorize("!hasRole('ROLE_ADVENTUROUS')")
    public ResponseEntity<PublicationResource> createPublication(@RequestBody CreatePublicationResource resource) {
        Long entrepreneurId = SecurityUtils.getCurrentUserId();
        var createPublicationCommand = CreatePublicationCommandFromResourceAssembler.toCommandFromResource(resource, entrepreneurId);
        var publication = publicationCommandService.handle(createPublicationCommand);
        if (publication == null) {
            return ResponseEntity.badRequest().build();
        }
        var publicationResource = PublicationResourceFromEntityAssembler.
                toResourceFromEntity(publication);
        return new ResponseEntity<>(publicationResource, HttpStatus.CREATED);
    }

    @PutMapping("/{publicationId}/update-publication")
    public ResponseEntity<PublicationResource> updatePublication(@PathVariable Long publicationId, @RequestBody UpdatePublicationResource resource) {
        if (publicationId == null) {
            return ResponseEntity.badRequest().build();
        }
        Long entrepreneurId = SecurityUtils.getCurrentUserId();
        var updatePublicationCommand = UpdatePublicationCommandFromResourceAssembler.toCommandFromResource(publicationId, entrepreneurId, resource);
        Optional<Publication> publicationOptional = publicationCommandService.handle(updatePublicationCommand);
        if (publicationOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var publicationResource = PublicationResourceFromEntityAssembler.toResourceFromEntity(publicationOptional.get());
        return new ResponseEntity<>(publicationResource, HttpStatus.CREATED);
    }


    @DeleteMapping("/{publicationId}/delete-publication")
    public ResponseEntity<Void> deletePublication(@PathVariable Long publicationId) {
        if (publicationId == null) {
            return ResponseEntity.badRequest().build();
        }
        publicationCommandService.handle(new DeletePublicationCommand(publicationId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{publicationId}/add-comment")
    public ResponseEntity<CommentResource> addCommentToPublication(@PathVariable Long publicationId, @RequestBody AddCommentToPublicationResource resource) {
        if (publicationId == null) {
            return ResponseEntity.badRequest().build();
        }
        var addCommentToPublicationResource = new AddCommentToPublicationResource(publicationId, resource.content(), resource.rating(), resource.adventureId());
        var addCommentToPublicationCommand = AddCommentCommandFromResourceAssembler.toCommandFromResource(addCommentToPublicationResource);
        var comment = publicationCommandService.handle(addCommentToPublicationCommand);
        if (comment == null) {
            return ResponseEntity.badRequest().build();
        }
        var commentResource = CommentResouceFromEntityAssembler.toResourceFromEntity(comment);
        return new ResponseEntity<>(commentResource, HttpStatus.CREATED);
    }

    @GetMapping("/all-publications")
    public ResponseEntity<List<PublicationResource>> getAllPublications() {
        var getAllPublicationsQuery = new GetAllPublicationsQuery();
        var publications = publicationQueryService.handle(getAllPublicationsQuery);
        var publicationResources = publications.stream().map(PublicationResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(publicationResources, HttpStatus.OK);
    }

    @GetMapping("/{publicationId}")
    public ResponseEntity<PublicationResource> getPublicationById(@PathVariable Long publicationId) {
        if (publicationId == null) {
            return ResponseEntity.badRequest().build();
        }
        var getPublicationByIdQuery = new GetPublicationByIdQuery(publicationId);
        var publication = publicationQueryService.handle(getPublicationByIdQuery);
        if (publication.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var publicationResource = PublicationResourceFromEntityAssembler.toResourceFromEntity(publication.get());
        return new ResponseEntity<>(publicationResource, HttpStatus.OK);
    }

    @GetMapping("/{entrepreneurId}/publications")
    public ResponseEntity<List<PublicationResource>> getPublicationsByEntrepreneurId(@PathVariable Long entrepreneurId) {
        if (entrepreneurId == null) {
            return ResponseEntity.badRequest().build();
        }
        var getPublicationsByEntrepreneurIdQuery = new GetPublicationByEntrepeneurIdQuery(entrepreneurId);
        var publications = publicationQueryService.handle(getPublicationsByEntrepreneurIdQuery);
        var publicationResources = publications.stream().map(PublicationResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(publicationResources, HttpStatus.OK);
    }

    @GetMapping("/all-comments")
    public ResponseEntity<List<CommentResource>> getAllComments() {
        var getAllCommentsQuery = new GetAllCommentsQuery();
        var comments = publicationQueryService.handle(getAllCommentsQuery);
        var commentResources = comments.stream().map(CommentResouceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(commentResources, HttpStatus.OK);
    }

    @GetMapping("/{publicationId}/comments")
    public ResponseEntity<List<CommentResource>> getCommentsByPublicationId(@PathVariable Long publicationId) {
        if (publicationId == null) {
            return ResponseEntity.badRequest().build();
        }
        var getCommentsByPublicationIdQuery = new GetCommentsByPublicationIdQuery(publicationId);
        var comments = publicationQueryService.handle(getCommentsByPublicationIdQuery);
        var commentResources = comments.stream().flatMap(List::stream).map(CommentResouceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(commentResources, HttpStatus.OK);
    }

    @GetMapping("/order-by-rating/{entrepreneurId}")
    public ResponseEntity<List<PublicationByOrderResource>> getFavoritePublicationsByProfileIdOrderedByRating(@PathVariable Long entrepreneurId) {
        if (entrepreneurId == null) {
            return ResponseEntity.badRequest().build();
        }
        GetFavoritePublicationsByProfileIdOrderedByRatingQuery query = new GetFavoritePublicationsByProfileIdOrderedByRatingQuery(entrepreneurId);
        List<Publication> publications = publicationQueryService.handle(query);
        List<PublicationByOrderResource> publicationResources = publications.stream()
                .map(PublicationByOrderResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(publicationResources, HttpStatus.OK);
    }

    @DeleteMapping("/{publicationId}/comments/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long publicationId,
            @PathVariable Long commentId) {
        try {
            var resource = new DeleteCommentResource(publicationId, commentId);
            var command = DeleteCommentCommandFromResourceAssembler.toCommandFromResource(resource);
            publicationCommandService.handle(command);
            return ResponseEntity.ok("Comment deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}