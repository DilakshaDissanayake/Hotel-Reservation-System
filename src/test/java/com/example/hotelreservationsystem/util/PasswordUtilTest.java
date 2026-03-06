package com.example.hotelreservationsystem.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PasswordUtil - BCrypt password hashing and verification.
 * Requirement: NFR4 – Security (TC08 – Password Security)
 * Passwords shall be stored using a secure one-way hashing algorithm (bcrypt)
 */
class PasswordUtilTest {

    @Test
    @DisplayName("TC08.1 – Hash should generate BCrypt format (not plain text)")
    void hash_shouldGenerateBCryptFormat() {
        String password = "admin123";
        String hash = PasswordUtil.hash(password);

        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$"), "Hash should start with BCrypt prefix $2a$");
        assertNotEquals(password, hash, "Hash must not equal plain text password");
    }

    @Test
    @DisplayName("TC08.2 – Hash should generate different hash each time (unique salt)")
    void hash_shouldGenerateDifferentHashEachTime() {
        String password = "admin123";

        String hash1 = PasswordUtil.hash(password);
        String hash2 = PasswordUtil.hash(password);

        assertNotEquals(hash1, hash2, "Each hash should use a unique salt");
    }

    @Test
    @DisplayName("TC08.3 – Verify should return true for correct password")
    void verify_shouldReturnTrueForCorrectPassword() {
        String password = "admin123";
        String hash = PasswordUtil.hash(password);

        assertTrue(PasswordUtil.verify(password, hash));
    }

    @Test
    @DisplayName("TC08.4 – Verify should return false for wrong password")
    void verify_shouldReturnFalseForWrongPassword() {
        String hash = PasswordUtil.hash("admin123");

        assertFalse(PasswordUtil.verify("wrong", hash));
    }

    @Test
    @DisplayName("TC08.5 – Verify should return false for null hash")
    void verify_shouldReturnFalseForNullHash() {
        assertFalse(PasswordUtil.verify("admin123", null));
    }

    @Test
    @DisplayName("TC08.6 – Hash should handle long passwords")
    void hash_shouldHandleLongPasswords() {
        String longPassword = "a".repeat(100);
        String hash = PasswordUtil.hash(longPassword);

        assertNotNull(hash);
        assertTrue(PasswordUtil.verify(longPassword, hash));
    }

    @Test
    @DisplayName("TC08.7 – Hash should handle special characters")
    void hash_shouldHandleSpecialCharacters() {
        String specialPassword = "P@$$w0rd!#%&*()";
        String hash = PasswordUtil.hash(specialPassword);

        assertNotNull(hash);
        assertTrue(PasswordUtil.verify(specialPassword, hash));
    }

    @Test
    @DisplayName("TC08.8 – Verify should be case-sensitive")
    void verify_shouldBeCaseSensitive() {
        String hash = PasswordUtil.hash("Password123");

        assertFalse(PasswordUtil.verify("password123", hash));
        assertFalse(PasswordUtil.verify("PASSWORD123", hash));
        assertTrue(PasswordUtil.verify("Password123", hash));
    }
}