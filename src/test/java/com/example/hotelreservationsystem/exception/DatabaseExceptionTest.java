package com.example.hotelreservationsystem.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DatabaseException custom runtime exception.
 * Requirement: NFR3 – Reliability (graceful error handling)
 */
class DatabaseExceptionTest {

    @Test
    @DisplayName("Should create exception with message and cause")
    void constructor_shouldSetMessageAndCause() {
        RuntimeException cause = new RuntimeException("SQL error");
        DatabaseException exception = new DatabaseException("Failed to insert record", cause);

        assertEquals("Failed to insert record", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void shouldBeRuntimeException() {
        DatabaseException exception = new DatabaseException("Error", new RuntimeException());

        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("Should preserve cause chain")
    void shouldPreserveCauseChain() {
        Exception rootCause = new java.sql.SQLException("Connection refused");
        DatabaseException exception = new DatabaseException("Database error", rootCause);

        assertInstanceOf(java.sql.SQLException.class, exception.getCause());
        assertEquals("Connection refused", exception.getCause().getMessage());
    }
}
