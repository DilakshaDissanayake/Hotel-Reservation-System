package com.example.hotelreservationsystem.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ReservationSummaryDTO.
 * Requirement: FR3 – Display Reservation
 */
class ReservationSummaryDTOTest {

    @Test
    @DisplayName("Default constructor should create empty summary")
    void defaultConstructor_shouldCreateEmptySummary() {
        ReservationSummaryDTO dto = new ReservationSummaryDTO();

        assertEquals(0, dto.getReservationId());
        assertEquals(0, dto.getGuestCount());
        assertNull(dto.getContactNumber());
        assertNull(dto.getCheckInDate());
        assertNull(dto.getCheckOutDate());
        assertEquals(0, dto.getRoomId());
        assertNull(dto.getRoomNumber());
        assertNull(dto.getRoomType());
        assertEquals(0.0, dto.getRatePerNight());
        assertNull(dto.getGuestName());
        assertNull(dto.getGuestEmail());
        assertNull(dto.getStatus());
    }

    @Test
    @DisplayName("Parameterized constructor should set all fields")
    void parameterizedConstructor_shouldSetAllFields() {
        ReservationSummaryDTO dto = new ReservationSummaryDTO(
                1001L, 2, "0771234567", "2026-03-10", "2026-03-13",
                1, "R101", "DELUXE", 15000.0, "John Doe", "john@test.com", "CONFIRMED");

        assertEquals(1001L, dto.getReservationId());
        assertEquals(2, dto.getGuestCount());
        assertEquals("0771234567", dto.getContactNumber());
        assertEquals("2026-03-10", dto.getCheckInDate());
        assertEquals("2026-03-13", dto.getCheckOutDate());
        assertEquals(1, dto.getRoomId());
        assertEquals("R101", dto.getRoomNumber());
        assertEquals("DELUXE", dto.getRoomType());
        assertEquals(15000.0, dto.getRatePerNight());
        assertEquals("John Doe", dto.getGuestName());
        assertEquals("john@test.com", dto.getGuestEmail());
        assertEquals("CONFIRMED", dto.getStatus());
    }

    @Test
    @DisplayName("Setters should update all fields correctly")
    void setters_shouldUpdateAllFields() {
        ReservationSummaryDTO dto = new ReservationSummaryDTO();
        dto.setReservationId(2002L);
        dto.setGuestCount(3);
        dto.setContactNumber("0779876543");
        dto.setCheckInDate("2026-04-01");
        dto.setCheckOutDate("2026-04-05");
        dto.setRoomId(5);
        dto.setRoomNumber("R301");
        dto.setRoomType("SUITE");
        dto.setRatePerNight(25000.0);
        dto.setGuestName("Jane Smith");
        dto.setGuestEmail("jane@test.com");
        dto.setStatus("COMPLETED");

        assertEquals(2002L, dto.getReservationId());
        assertEquals(3, dto.getGuestCount());
        assertEquals("0779876543", dto.getContactNumber());
        assertEquals("2026-04-01", dto.getCheckInDate());
        assertEquals("2026-04-05", dto.getCheckOutDate());
        assertEquals(5, dto.getRoomId());
        assertEquals("R301", dto.getRoomNumber());
        assertEquals("SUITE", dto.getRoomType());
        assertEquals(25000.0, dto.getRatePerNight());
        assertEquals("Jane Smith", dto.getGuestName());
        assertEquals("jane@test.com", dto.getGuestEmail());
        assertEquals("COMPLETED", dto.getStatus());
    }

    @Test
    @DisplayName("Reservation summary should contain guest and room details (FR3)")
    void reservationSummary_shouldContainGuestAndRoomDetails() {
        // FR3: Displayed information includes guest details, room info, dates
        ReservationSummaryDTO dto = new ReservationSummaryDTO(
                1001L, 2, "0771234567", "2026-03-10", "2026-03-13",
                1, "R101", "DELUXE", 15000.0, "John Doe", "john@test.com", "CONFIRMED");

        assertNotNull(dto.getGuestName());
        assertNotNull(dto.getRoomNumber());
        assertNotNull(dto.getRoomType());
        assertNotNull(dto.getCheckInDate());
        assertNotNull(dto.getCheckOutDate());
    }
}
