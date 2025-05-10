package com.upc.aventurape.platform.iam.domain.model.commands;

import com.upc.aventurape.platform.iam.domain.model.entities.Role;

import java.util.List;

public record SignUpCommand(String username, String password, String email, List<Role> roles) {
}
