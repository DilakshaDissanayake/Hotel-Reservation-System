package com.example.hotelreservationsystem.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BillDetailsDTO.
 * Covers: constructors, getters, setters, getRoomCharges() convenience method
 * Requirement: FR4 – Billing Calculation, FR5 – Bill Printing
 */
class BillDetailsDTOTest {

    @Test
    @DisplayName("Default constructor should create empty bill DTO")
    void defaultConstructor_shouldCreateEmptyDTO() {
        BillDetailsDTO dto = new BillDetailsDTO();

        assertEquals(0, dto.getBillId());
        assertEquals(0, dto.getReservationId());
        assertNull(dto.getGuestName());
        assertNull(dto.getGuestEmail());
        assertNull(dto.getRoomNumber());
        assertNull(dto.getRoomType());
        assertNull(dto.getCheckInDate());
        assertNull(dto.getCheckOutDate());
        assertEquals(0, dto.getNights());
        assertEquals(0.0, dto.getRatePerNight());
        assertEquals(0.0, dto.getExtrasTotal());
        assertEquals(0.0, dto.getDiscountAmount());
        assertEquals(0.0, dto.getSubTotal());
        assertEquals(0.0, dto.getTotal());
        assertNull(dto.getGeneratedAt());
    }

    @Test
    @DisplayName("Parameterized constructor should set all fields")
    void parameterizedConstructor_shouldSetAllFields() {
        BillDetailsDTO dto = new BillDetailsDTO(
                1, 1001L, "John Doe", "john@test.com", "R101", "DELUXE",
                "2026-03-10", "2026-03-13", 3, 5000.0, 2000.0, 1500.0,
                17000.0, 15500.0, "2026-03-05");

        assertEquals(1, dto.getBillId());
        assertEquals(1001L, dto.getReservationId());
        assertEquals("John Doe", dto.getGuestName());
        assertEquals("john@test.com", dto.getGuestEmail());
        assertEquals("R101", dto.getRoomNumber());
        assertEquals("DELUXE", dto.getRoomType());
        assertEquals("2026-03-10", dto.getCheckInDate());
        assertEquals("2026-03-13", dto.getCheckOutDate());
        assertEquals(3, dto.getNights());
        assertEquals(5000.0, dto.getRatePerNight());
        assertEquals(2000.0, dto.getExtrasTotal());
        assertEquals(1500.0, dto.getDiscountAmount());
        assertEquals(17000.0, dto.getSubTotal());
        assertEquals(15500.0, dto.getTotal());
        assertEquals("2026-03-05", dto.getGeneratedAt());
    }

    @Test
    @DisplayName("getRoomCharges should calculate ratePerNight × nights")
    void getRoomCharges_shouldCalculateCorrectly() {
        BillDetailsDTO dto = new BillDetailsDTO();
        dto.setRatePerNight(5000.0);
        dto.setNights(3);

        assertEquals(15000.0, dto.getRoomCharges());
    }

    @Test
    @DisplayName("getRoomCharges should return zero when nights is zero")
    void getRoomCharges_shouldReturnZero_whenNightsZero() {
        BillDetailsDTO dto = new BillDetailsDTO();
        dto.setRatePerNight(5000.0);
        dto.setNights(0);

        assertEquals(0.0, dto.getRoomCharges());
    }

    @Test
    @DisplayName("Setters should update all fields correctly")
    void setters_shouldUpdateAllFields() {
        BillDetailsDTO dto = new BillDetailsDTO();
        dto.setBillId(2);
        dto.setReservationId(2002L);
        dto.setGuestName("Jane Smith");
        dto.setGuestEmail("jane@test.com");
        dto.setRoomNumber("R202");
        dto.setRoomType("SUITE");
        dto.setCheckInDate("2026-04-01");
        dto.setCheckOutDate("2026-04-05");
        dto.setNights(4);
        dto.setRatePerNight(25000.0);
        dto.setExtrasTotal(5000.0);
        dto.setDiscountAmount(10000.0);
        dto.setSubTotal(105000.0);
        dto.setTotal(95000.0);
        dto.setGeneratedAt("2026-03-30");

        assertEquals(2, dto.getBillId());
        assertEquals(2002L, dto.getReservationId());
        assertEquals("Jane Smith", dto.getGuestName());
        assertEquals("jane@test.com", dto.getGuestEmail());
        assertEquals("R202", dto.getRoomNumber());
        assertEquals("SUITE", dto.getRoomType());
        assertEquals(4, dto.getNights());
        assertEquals(25000.0, dto.getRatePerNight());
        assertEquals(100000.0, dto.getRoomCharges()); // 25000 * 4
    }
}
