package com.example.hotelreservationsystem.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User model entity.
 * Covers: constructors, getters, setters for all fields.
 * Requirement: NFR5 – Maintainability (model layer integrity)
 */
class UserTest {

    @Test
    void defaultConstructor_shouldCreateEmptyUser() {
        User user = new User();

        assertEquals(0, user.getId());
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getPasswordHash());
        assertNull(user.getRole());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }

    @Test
    void parameterizedConstructor_shouldSetAllFields() {
        User user = new User(1, "John", "Doe", "johndoe", "john@example.com",
                "$2a$12$hashedpassword", "ADMIN", "2026-01-01", "2026-01-02");

        assertEquals(1, user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("johndoe", user.getUsername());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("$2a$12$hashedpassword", user.getPasswordHash());
        assertEquals("ADMIN", user.getRole());
        assertEquals("2026-01-01", user.getCreatedAt());
        assertEquals("2026-01-02", user.getUpdatedAt());
    }

    @Test
    void setters_shouldUpdateFields() {
        User user = new User();
        user.setId(5);
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setUsername("janesmith");
        user.setEmail("jane@example.com");
        user.setPasswordHash("newHash");
        user.setRole("RECEPTIONIST");
        user.setCreatedAt("2026-03-01");
        user.setUpdatedAt("2026-03-05");

        assertEquals(5, user.getId());
        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("janesmith", user.getUsername());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals("newHash", user.getPasswordHash());
        assertEquals("RECEPTIONIST", user.getRole());
        assertEquals("2026-03-01", user.getCreatedAt());
        assertEquals("2026-03-05", user.getUpdatedAt());
    }

    @Test
    void userRoles_shouldSupportAdminAndReceptionist() {
        // A4 – User Roles assumption
        User admin = new User();
        admin.setRole("ADMIN");
        assertEquals("ADMIN", admin.getRole());

        User receptionist = new User();
        receptionist.setRole("RECEPTIONIST");
        assertEquals("RECEPTIONIST", receptionist.getRole());
    }
}
