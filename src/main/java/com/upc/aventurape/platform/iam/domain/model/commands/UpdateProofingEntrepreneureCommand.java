package com.upc.aventurape.platform.iam.domain.model.commands;

public record UpdateProofingEntrepreneureCommand(
        Long userId,
        String proofingEntrepreneure
) {
    public UpdateProofingEntrepreneureCommand {
        if (userId == null) {
            throw new IllegalArgumentException("UserId must not be null");
        }
        if (proofingEntrepreneure == null || proofingEntrepreneure.isBlank()) {
            throw new IllegalArgumentException("ProofingEntrepreneure must not be null or blank");
        }
    }
}