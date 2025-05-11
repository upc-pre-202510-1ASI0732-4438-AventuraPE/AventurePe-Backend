package com.upc.aventurape.platform.iam.interfaces.rest.transform;

import com.upc.aventurape.platform.iam.domain.model.commands.UpdateProofingEntrepreneureCommand;
import com.upc.aventurape.platform.iam.interfaces.rest.resources.UpdateProofingEntrepreneureResource;

public class UpdateProofingEntrepreneureCommandFromResourceAssembler {
    public static UpdateProofingEntrepreneureCommand toCommandFromResource(Long userId, UpdateProofingEntrepreneureResource resource) {
        return new UpdateProofingEntrepreneureCommand(
                userId,
                resource.proofingEntrepreneure()
        );
    }
}