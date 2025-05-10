package com.upc.aventurape.platform.profiles.domain.model;

import com.upc.aventurape.platform.profiles.domain.model.aggregates.Profile;
import com.upc.aventurape.platform.profiles.domain.model.aggregates.ProfileAdventurer;
import com.upc.aventurape.platform.profiles.domain.model.aggregates.ProfileEntrepreneur;
import com.upc.aventurape.platform.profiles.domain.model.valueobjects.UserId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProfileTest {

    @Test
    public void testProfileCreation() {
        // Arrange
        Long id = 1L;
        String email = "john.doe@example.com";
        String street = "Main St";
        String number = "123";
        String city = "New York";
        String postalCode = "10001";
        String country = "USA";

        // Act
        Profile profile = new Profile(email, street, number, city, postalCode, country);
        profile.setId(id);

        // Assert
        assertEquals(id, profile.getId());
        assertEquals(email, profile.getEmailAddress());
        assertEquals(street, profile.getStreetAddress());
    }

    @Test
    public void testProfileAdventurerCreation() {
        // Arrange
        Long userId = 1L;
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@example.com";
        String street = "Main St";
        String number = "123";
        String city = "New York";
        String postalCode = "10001";
        String country = "USA";
        String gender = "MALE";

        // Act
        ProfileAdventurer adventurer = new ProfileAdventurer(
                new UserId(userId),
                firstName,
                lastName,
                email,
                street,
                number,
                city,
                postalCode,
                country,
                gender
        );

        // Assert
        assertEquals(userId, adventurer.getUserId().userId());
        assertEquals(firstName, adventurer.getFirstName());
        assertEquals(lastName, adventurer.getLastName());
        assertEquals(gender, adventurer.getGender());
        assertEquals(email, adventurer.getEmailAddress());
        assertEquals(street, adventurer.getStreetAddress());
    }

    @Test
    public void testProfileEntrepreneurCreation() {
        // Arrange
        Long userId = 1L;
        String email = "jane.smith@example.com";
        String street = "Oak St";
        String number = "456";
        String city = "Los Angeles";
        String postalCode = "90001";
        String country = "USA";
        String businessName = "Adventure Tours";

        // Act
        ProfileEntrepreneur entrepreneur = new ProfileEntrepreneur(
                new UserId(userId),
                email,
                street,
                number,
                city,
                postalCode,
                country,
                businessName
        );

        // Assert
        assertEquals(userId, entrepreneur.getUserId().userId());
        assertEquals(businessName, entrepreneur.getName());
        assertEquals(email, entrepreneur.getEmail());
        assertEquals(street, entrepreneur.getStreet());
        assertEquals(number, entrepreneur.getNumber());
        assertEquals(city, entrepreneur.getCity());
        assertEquals(postalCode, entrepreneur.getPostalCode());
        assertEquals(country, entrepreneur.getCountry());
    }
} 