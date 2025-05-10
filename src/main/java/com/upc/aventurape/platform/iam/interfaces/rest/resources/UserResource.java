package com.upc.aventurape.platform.iam.interfaces.rest.resources;

import java.util.List;

public record UserResource(Long id, String username, String email, List<String> roles, String proofingEntrepreneure) {
}
