package com.example.hotelreservationsystem.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RoomFacilities model entity (many-to-many junction).
 * Requirement: A9 – Room Facilities & Extras with extra charges per night
 */
class RoomFacilitiesTest {

    @Test
    void defaultConstructor_shouldCreateEmptyRoomFacility() {
        RoomFacilities rf = new RoomFacilities();

        assertEquals(0, rf.getRoomId());
        assertEquals(0, rf.getFacilityId());
        assertEquals(0.0, rf.getExtraPricePerNight());
    }

    @Test
    void parameterizedConstructor_shouldSetAllFields() {
        RoomFacilities rf = new RoomFacilities(1, 3, 1500.0);

        assertEquals(1, rf.getRoomId());
        assertEquals(3, rf.getFacilityId());
        assertEquals(1500.0, rf.getExtraPricePerNight());
    }

    @Test
    void setters_shouldUpdateFields() {
        RoomFacilities rf = new RoomFacilities();
        rf.setRoomId(5);
        rf.setFacilityId(2);
        rf.setExtraPricePerNight(2500.0);

        assertEquals(5, rf.getRoomId());
        assertEquals(2, rf.getFacilityId());
        assertEquals(2500.0, rf.getExtraPricePerNight());
    }

    @Test
    void extraPricePerNight_shouldSupportAdditionalCharges() {
        // A9 – rooms may have optional facilities with extra charges per night
        RoomFacilities rf = new RoomFacilities(1, 1, 500.0);
        assertTrue(rf.getExtraPricePerNight() > 0);
    }
}
