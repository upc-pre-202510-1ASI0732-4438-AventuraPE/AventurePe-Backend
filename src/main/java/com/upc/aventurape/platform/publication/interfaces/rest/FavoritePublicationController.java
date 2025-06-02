// src/main/java/com/upc/aventurape/platform/publication/interfaces/rest/FavoritePublicationController.java
package com.upc.aventurape.platform.publication.interfaces.rest;

import com.upc.aventurape.platform.iam.infrastructure.security.SecurityUtils;
import com.upc.aventurape.platform.publication.application.internal.outboundservices.acl.ExternalProfileService;
import com.upc.aventurape.platform.publication.domain.model.commands.DeleteFavoriteCommand;
import com.upc.aventurape.platform.publication.domain.model.queries.GetAllFavoritePublicationsQuery;
import com.upc.aventurape.platform.publication.domain.model.queries.GetFavoritePublicationByProfileIdQuery;
import com.upc.aventurape.platform.publication.domain.model.valueobjects.ProfileId;
import com.upc.aventurape.platform.publication.domain.services.FavoritePublicationCommandService;
import com.upc.aventurape.platform.publication.domain.services.FavoritePublicationQueryService;
import com.upc.aventurape.platform.publication.interfaces.rest.resources.FavoriteResource;
import com.upc.aventurape.platform.publication.interfaces.rest.resources.CreateFavoritePublicationResource;
import com.upc.aventurape.platform.publication.interfaces.rest.transform.FavoritePublicationResourceFromEntityAssembler;
import com.upc.aventurape.platform.publication.interfaces.rest.transform.CreateFavoritePublicationCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// src/main/java/com/upc/aventurape/platform/publication/interfaces/rest/FavoritePublicationController.java

@RestController
@RequestMapping(value= "/api/v1/favorite-publications", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Favorite Publications")
public class FavoritePublicationController {

    private final FavoritePublicationCommandService favoriteCommandService;
    private final FavoritePublicationQueryService favoriteQueryService;

    public FavoritePublicationController(FavoritePublicationCommandService favoriteCommandService,
                                         FavoritePublicationQueryService favoriteQueryService) {
        this.favoriteCommandService = favoriteCommandService;
        this.favoriteQueryService = favoriteQueryService;
    }

    @GetMapping("/getAllFavoritesPublications")
    public ResponseEntity<List<FavoriteResource>> getAllFavoritePublications() {
        var getAllFavoritePublicationsQuery = new GetAllFavoritePublicationsQuery();
        var favoritePublications = favoriteQueryService.handle(getAllFavoritePublicationsQuery);
        var publicationResources = favoritePublications.stream()
                .map(FavoritePublicationResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(publicationResources, HttpStatus.OK);
    }

    @GetMapping("/getFavoritePublicationByProfileId/{profileId}")
    public ResponseEntity<List<FavoriteResource>> getFavoritePublicationByProfileId(@PathVariable Long profileId) {
        if (profileId == null) {
            return ResponseEntity.badRequest().build();
        }
        var profileIdLong=new ProfileId(profileId);
        var getFavoritePublicationByProfileIdQuery = new GetFavoritePublicationByProfileIdQuery(profileIdLong);
        var favoritePublications = favoriteQueryService.handle(getFavoritePublicationByProfileIdQuery);
        var publicationResources = favoritePublications.stream()
                .map(FavoritePublicationResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(publicationResources, HttpStatus.OK);
    }



    @PostMapping("/create-favorite-publication")
    public ResponseEntity<FavoriteResource> createFavoritePublication(@RequestBody CreateFavoritePublicationResource resource) {
        Long profileId = SecurityUtils.getCurrentUserId();
        var createFavoritePublicationCommand = CreateFavoritePublicationCommandFromResourceAssembler.toCommandFromResource(resource, profileId);
        var favoritePublication = favoriteCommandService.handle(createFavoritePublicationCommand);
        var favoriteResource = FavoritePublicationResourceFromEntityAssembler.toResourceFromEntity(favoritePublication);
        return new ResponseEntity<>(favoriteResource, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-favorite-publication/{id}")
    public ResponseEntity<Void> deleteFavoritePublication(@PathVariable Long id) {
        favoriteCommandService.handle(new DeleteFavoriteCommand(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count-by-publication/{publicationId}")
    public ResponseEntity<Long> getCountByPublicationId(@PathVariable Long publicationId) {
        if (publicationId == null) {
            return ResponseEntity.badRequest().build();
        }
        var count = favoriteQueryService.getCountByPublicationId(publicationId);
        return ResponseEntity.ok(count);
    }

}