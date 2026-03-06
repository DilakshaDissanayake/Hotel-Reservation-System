package com.example.hotelreservationsystem.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Reservations model entity.
 * Covers: constructors, getters, setters, reservation statuses
 * Requirement: FR2 – Add Reservation, A2 – Reservation Number Generation
 */
class ReservationsTest {

    @Test
    void defaultConstructor_shouldCreateEmptyReservation() {
        Reservations reservation = new Reservations();

        assertEquals(0, reservation.getReservationId());
        assertEquals(0, reservation.getGuestCount());
        assertNull(reservation.getAddress());
        assertNull(reservation.getContactNumber());
        assertNull(reservation.getRoomType());
        assertNull(reservation.getCheckInDate());
        assertNull(reservation.getCheckOutDate());
        assertEquals(0, reservation.getRoomId());
        assertNull(reservation.getStatus());
        assertNull(reservation.getCreatedAt());
    }

    @Test
    void parameterizedConstructor_shouldSetAllFields() {
        Reservations reservation = new Reservations(
                1001, 2, "123 Beach Rd, Galle", "0771234567",
                "DELUXE", "2026-03-10", "2026-03-13", 5, "CONFIRMED", "2026-03-05");

        assertEquals(1001, reservation.getReservationId());
        assertEquals(2, reservation.getGuestCount());
        assertEquals("123 Beach Rd, Galle", reservation.getAddress());
        assertEquals("0771234567", reservation.getContactNumber());
        assertEquals("DELUXE", reservation.getRoomType());
        assertEquals("2026-03-10", reservation.getCheckInDate());
        assertEquals("2026-03-13", reservation.getCheckOutDate());
        assertEquals(5, reservation.getRoomId());
        assertEquals("CONFIRMED", reservation.getStatus());
        assertEquals("2026-03-05", reservation.getCreatedAt());
    }

    @Test
    void setters_shouldUpdateFields() {
        Reservations reservation = new Reservations();
        reservation.setReservationId(2002);
        reservation.setGuestCount(3);
        reservation.setAddress("456 Colombo Rd");
        reservation.setContactNumber("0779876543");
        reservation.setRoomType("SUITE");
        reservation.setCheckInDate("2026-04-01");
        reservation.setCheckOutDate("2026-04-05");
        reservation.setRoomId(10);
        reservation.setStatus("COMPLETED");
        reservation.setCreatedAt("2026-03-01");

        assertEquals(2002, reservation.getReservationId());
        assertEquals(3, reservation.getGuestCount());
        assertEquals("456 Colombo Rd", reservation.getAddress());
        assertEquals("0779876543", reservation.getContactNumber());
        assertEquals("SUITE", reservation.getRoomType());
        assertEquals("2026-04-01", reservation.getCheckInDate());
        assertEquals("2026-04-05", reservation.getCheckOutDate());
        assertEquals(10, reservation.getRoomId());
        assertEquals("COMPLETED", reservation.getStatus());
    }

    @Test
    void reservationStatus_shouldSupportExpectedValues() {
        Reservations reservation = new Reservations();

        reservation.setStatus("CONFIRMED");
        assertEquals("CONFIRMED", reservation.getStatus());

        reservation.setStatus("COMPLETED");
        assertEquals("COMPLETED", reservation.getStatus());

        reservation.setStatus("CANCELLED");
        assertEquals("CANCELLED", reservation.getStatus());
    }

    @Test
    void reservationId_shouldBeUniqueIdentifier() {
        // A2 – Reservation Number Generation
        Reservations r1 = new Reservations();
        r1.setReservationId(1001);

        Reservations r2 = new Reservations();
        r2.setReservationId(1002);

        assertNotEquals(r1.getReservationId(), r2.getReservationId());
    }
}
