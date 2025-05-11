package com.upc.aventurape.platform.iam.interfaces.rest.transform;

import com.upc.aventurape.platform.iam.domain.model.aggregates.User;
import com.upc.aventurape.platform.iam.domain.model.entities.Role;
import com.upc.aventurape.platform.iam.interfaces.rest.resources.UserResource;

public class UserResourceFromEntityAssembler {

  public static UserResource toResourceFromEntity(User user) {
    var roles = user.getRoles().stream()
        .map(Role::getStringName)
        .toList();
    return new UserResource(user.getId(), user.getUsername(), user.getEmail(), roles, user.getProofingEntrepreneure());
  }
}
