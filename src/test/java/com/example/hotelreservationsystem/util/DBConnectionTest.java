package com.example.hotelreservationsystem.util;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class DBConnectionTest {

    @Test
    void testDatabaseConnectionIsSuccessful() {
        try (Connection connection = DBConnection.getConnection()) {

            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");

        } catch (Exception e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }
}
