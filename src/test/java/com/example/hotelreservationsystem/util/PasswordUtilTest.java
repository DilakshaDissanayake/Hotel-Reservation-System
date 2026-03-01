package com.example.hotelreservationsystem.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void hash_shouldGenerateDifferentHashEachTime() {
        String password = "admin123";

        String hash1 = PasswordUtil.hash(password);
        String hash2 = PasswordUtil.hash(password);

        assertNotEquals(hash1, hash2);
    }

    @Test
    void verify_shouldReturnTrueForCorrectPassword() {
        String password = "admin123";
        String hash = PasswordUtil.hash(password);

        assertTrue(PasswordUtil.verify(password, hash));
    }

    @Test
    void verify_shouldReturnFalseForWrongPassword() {
        String hash = PasswordUtil.hash("admin123");

        assertFalse(PasswordUtil.verify("wrong", hash));
    }
}