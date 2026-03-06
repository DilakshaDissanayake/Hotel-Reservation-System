package com.example.hotelreservationsystem.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Rooms model entity.
 * Covers: constructors, getters, setters, room types (A1)
 * Requirement: A1 – Room Types (Single, Double, Deluxe, Suite)
 */
class RoomsTest {

    @Test
    void defaultConstructor_shouldCreateEmptyRoom() {
        Rooms room = new Rooms();

        assertEquals(0, room.getId());
        assertNull(room.getRoomNumber());
        assertNull(room.getRoomType());
        assertEquals(0.0, room.getRatePerNight());
        assertNull(room.getStatus());
        assertNull(room.getDescription());
        assertNull(room.getCreatedAt());
        assertNull(room.getUpdatedAt());
    }

    @Test
    void parameterizedConstructor_shouldSetAllFields() {
        Rooms room = new Rooms(1, "R101", "DELUXE", 15000.0,
                "AVAILABLE", "Ocean view room", "2026-01-01", "2026-01-02");

        assertEquals(1, room.getId());
        assertEquals("R101", room.getRoomNumber());
        assertEquals("DELUXE", room.getRoomType());
        assertEquals(15000.0, room.getRatePerNight());
        assertEquals("AVAILABLE", room.getStatus());
        assertEquals("Ocean view room", room.getDescription());
        assertEquals("2026-01-01", room.getCreatedAt());
        assertEquals("2026-01-02", room.getUpdatedAt());
    }

    @Test
    void setters_shouldUpdateFields() {
        Rooms room = new Rooms();
        room.setId(3);
        room.setRoomNumber("R202");
        room.setRoomType("SUITE");
        room.setRatePerNight(25000.0);
        room.setStatus("MAINTENANCE");
        room.setDescription("Luxury suite");
        room.setCreatedAt("2026-02-01");
        room.setUpdatedAt("2026-02-15");

        assertEquals(3, room.getId());
        assertEquals("R202", room.getRoomNumber());
        assertEquals("SUITE", room.getRoomType());
        assertEquals(25000.0, room.getRatePerNight());
        assertEquals("MAINTENANCE", room.getStatus());
        assertEquals("Luxury suite", room.getDescription());
    }

    @Test
    void roomTypes_shouldSupportAllPredefinedTypes() {
        // A1 – Room Types: Single, Double, Deluxe, Suite
        String[] validTypes = {"SINGLE", "DOUBLE", "DELUXE", "SUITE"};
        for (String type : validTypes) {
            Rooms room = new Rooms();
            room.setRoomType(type);
            assertEquals(type, room.getRoomType());
        }
    }

    @Test
    void roomRate_shouldSupportFixedRatePerNight() {
        // A1 – Each room type has a fixed rate per night
        Rooms singleRoom = new Rooms();
        singleRoom.setRatePerNight(5000.0);
        assertEquals(5000.0, singleRoom.getRatePerNight());

        Rooms suiteRoom = new Rooms();
        suiteRoom.setRatePerNight(25000.0);
        assertEquals(25000.0, suiteRoom.getRatePerNight());
    }
}
