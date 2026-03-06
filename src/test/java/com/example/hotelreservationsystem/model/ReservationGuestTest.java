package com.example.hotelreservationsystem.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ReservationGuest model entity.
 * Covers: constructors, getters, setters, NIC/passport, primary guest
 * Requirement: A8 – Guest Identification (primary NIC required, additional guests for security)
 */
class ReservationGuestTest {

    @Test
    void defaultConstructor_shouldCreateEmptyGuest() {
        ReservationGuest guest = new ReservationGuest();

        assertEquals(0, guest.getId());
        assertEquals(0, guest.getReservationId());
        assertNull(guest.getFullName());
        assertNull(guest.getAge());
        assertNull(guest.getGender());
        assertNull(guest.getNic());
        assertNull(guest.getPassportNumber());
        assertEquals(0, guest.getIsPrimary());
        assertNull(guest.getEmail());
        assertNull(guest.getPhoneNumber());
        assertNull(guest.getCreatedAt());
    }

    @Test
    void parameterizedConstructor_shouldSetAllFields() {
        ReservationGuest guest = new ReservationGuest(
                1, 1001, "John Doe", "30", "MALE",
                "200012345678", "N1234567", 1,
                "john@example.com", "0771234567", "2026-03-05");

        assertEquals(1, guest.getId());
        assertEquals(1001, guest.getReservationId());
        assertEquals("John Doe", guest.getFullName());
        assertEquals("30", guest.getAge());
        assertEquals("MALE", guest.getGender());
        assertEquals("200012345678", guest.getNic());
        assertEquals("N1234567", guest.getPassportNumber());
        assertEquals(1, guest.getIsPrimary());
        assertEquals("john@example.com", guest.getEmail());
        assertEquals("0771234567", guest.getPhoneNumber());
        assertEquals("2026-03-05", guest.getCreatedAt());
    }

    @Test
    void setters_shouldUpdateAllFields() {
        ReservationGuest guest = new ReservationGuest();
        guest.setId(5);
        guest.setReservationId(2002);
        guest.setFullName("Jane Smith");
        guest.setAge("25");
        guest.setGender("FEMALE");
        guest.setNic("199587654321");
        guest.setPassportNumber("P9876543");
        guest.setIsPrimary(0);
        guest.setEmail("jane@example.com");
        guest.setPhoneNumber("0779876543");
        guest.setCreatedAt("2026-03-10");

        assertEquals(5, guest.getId());
        assertEquals(2002, guest.getReservationId());
        assertEquals("Jane Smith", guest.getFullName());
        assertEquals("25", guest.getAge());
        assertEquals("FEMALE", guest.getGender());
        assertEquals("199587654321", guest.getNic());
        assertEquals("P9876543", guest.getPassportNumber());
        assertEquals(0, guest.getIsPrimary());
        assertEquals("jane@example.com", guest.getEmail());
        assertEquals("0779876543", guest.getPhoneNumber());
    }

    @Test
    void primaryGuest_shouldBeIdentifiedByFlag() {
        ReservationGuest primary = new ReservationGuest();
        primary.setIsPrimary(1);
        primary.setNic("200012345678");

        assertEquals(1, primary.getIsPrimary());
        assertNotNull(primary.getNic());

        // Additional guest for security
        ReservationGuest additional = new ReservationGuest();
        additional.setIsPrimary(0);

        assertEquals(0, additional.getIsPrimary());
    }
}
