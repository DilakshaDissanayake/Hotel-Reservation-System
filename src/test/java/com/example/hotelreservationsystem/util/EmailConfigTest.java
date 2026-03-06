package com.example.hotelreservationsystem.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EmailConfig validation.
 * Requirement: NFR3 – Reliability (graceful error handling for config)
 */
class EmailConfigTest {

    @Test
    @DisplayName("Valid config should not throw on validate")
    void validate_shouldNotThrow_whenAllFieldsPresent() {
        EmailConfig config = new EmailConfig(
                "smtp.gmail.com", "587", "user@gmail.com", "password", "user@gmail.com",
                true, true, false);

        assertDoesNotThrow(config::validate);
    }

    @Test
    @DisplayName("Should throw when SMTP host is missing")
    void validate_shouldThrow_whenSmtpHostMissing() {
        EmailConfig config = new EmailConfig(
                null, "587", "user@gmail.com", "password", "user@gmail.com",
                true, true, false);

        assertThrows(IllegalStateException.class, config::validate);
    }

    @Test
    @DisplayName("Should throw when SMTP port is missing")
    void validate_shouldThrow_whenSmtpPortMissing() {
        EmailConfig config = new EmailConfig(
                "smtp.gmail.com", null, "user@gmail.com", "password", "user@gmail.com",
                true, true, false);

        assertThrows(IllegalStateException.class, config::validate);
    }

    @Test
    @DisplayName("Should throw when username is missing")
    void validate_shouldThrow_whenUsernameMissing() {
        EmailConfig config = new EmailConfig(
                "smtp.gmail.com", "587", null, "password", "user@gmail.com",
                true, true, false);

        assertThrows(IllegalStateException.class, config::validate);
    }

    @Test
    @DisplayName("Should throw when password is missing")
    void validate_shouldThrow_whenPasswordMissing() {
        EmailConfig config = new EmailConfig(
                "smtp.gmail.com", "587", "user@gmail.com", null, "user@gmail.com",
                true, true, false);

        assertThrows(IllegalStateException.class, config::validate);
    }

    @Test
    @DisplayName("Should throw when from address is missing")
    void validate_shouldThrow_whenFromAddressMissing() {
        EmailConfig config = new EmailConfig(
                "smtp.gmail.com", "587", "user@gmail.com", "password", null,
                true, true, false);

        assertThrows(IllegalStateException.class, config::validate);
    }

    @Test
    @DisplayName("Should throw when field is blank (whitespace only)")
    void validate_shouldThrow_whenFieldIsBlank() {
        EmailConfig config = new EmailConfig(
                "   ", "587", "user@gmail.com", "password", "user@gmail.com",
                true, true, false);

        assertThrows(IllegalStateException.class, config::validate);
    }

    @Test
    @DisplayName("Getters should return correct values")
    void getters_shouldReturnCorrectValues() {
        EmailConfig config = new EmailConfig(
                "smtp.gmail.com", "587", "user@gmail.com", "pass123", "from@gmail.com",
                true, true, false);

        assertEquals("smtp.gmail.com", config.getSmtpHost());
        assertEquals("587", config.getSmtpPort());
        assertEquals("user@gmail.com", config.getUsername());
        assertEquals("pass123", config.getPassword());
        assertEquals("from@gmail.com", config.getFromAddress());
        assertTrue(config.isSmtpAuth());
        assertTrue(config.isStartTls());
        assertFalse(config.isSslEnable());
    }

    @Test
    @DisplayName("SSL enable flag should be configurable")
    void sslEnable_shouldBeConfigurable() {
        EmailConfig sslConfig = new EmailConfig(
                "smtp.gmail.com", "465", "user@gmail.com", "pass", "from@gmail.com",
                true, false, true);

        assertTrue(sslConfig.isSslEnable());
        assertFalse(sslConfig.isStartTls());
    }
}
