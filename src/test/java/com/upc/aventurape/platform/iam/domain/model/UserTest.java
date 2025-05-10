package com.upc.aventurape.platform.iam.domain.model;

import com.upc.aventurape.platform.iam.domain.model.aggregates.User;
import com.upc.aventurape.platform.iam.domain.model.entities.Role;
import com.upc.aventurape.platform.iam.domain.model.valueobjects.Roles;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testUserCreation() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String email = "test@example.com";

        // Act
        User user = new User(username, password, email);

        // Assert
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(email, user.getEmail());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    public void testAddRole() {
        // Arrange
        User user = new User("testuser", "password123", "test@example.com");
        Role role = new Role(Roles.ROLE_ADVENTUROUS);

        // Act
        user.addRole(role);

        // Assert
        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains(role));
    }

    @Test
    public void testAddRoles() {
        // Arrange
        User user = new User("testuser", "password123", "test@example.com");
        Role role1 = new Role(Roles.ROLE_ADVENTUROUS);
        Role role2 = new Role(Roles.ROLE_ADMIN);
        List<Role> roles = Arrays.asList(role1, role2);

        // Act
        user.addRoles(roles);

        // Assert
        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains(role1));
        assertTrue(user.getRoles().contains(role2));
    }

    @Test
    public void testUserDetails() {
        // Arrange
        User user = new User("testuser", "password123", "test@example.com");

        // Assert
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }
} 