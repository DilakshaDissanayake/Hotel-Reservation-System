package com.example.hotelreservationsystem.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DashboardStatsDTO.
 * Requirement: NFR5 – Maintainability (data transfer layer)
 */
class DashboardStatsDTOTest {

    @Test
    @DisplayName("Default constructor should create empty stats")
    void defaultConstructor_shouldCreateEmptyStats() {
        DashboardStatsDTO dto = new DashboardStatsDTO();

        assertEquals(0, dto.getTotalRooms());
        assertEquals(0, dto.getAvailableRooms());
        assertEquals(0, dto.getTotalReservations());
        assertEquals(0, dto.getTotalBills());
        assertEquals(0.0, dto.getTotalRevenue());
    }

    @Test
    @DisplayName("Parameterized constructor should set all fields")
    void parameterizedConstructor_shouldSetAllFields() {
        DashboardStatsDTO dto = new DashboardStatsDTO(10, 5, 20, 15, 750000.0);

        assertEquals(10, dto.getTotalRooms());
        assertEquals(5, dto.getAvailableRooms());
        assertEquals(20, dto.getTotalReservations());
        assertEquals(15, dto.getTotalBills());
        assertEquals(750000.0, dto.getTotalRevenue());
    }

    @Test
    @DisplayName("Setters should update all fields")
    void setters_shouldUpdateAllFields() {
        DashboardStatsDTO dto = new DashboardStatsDTO();
        dto.setTotalRooms(15);
        dto.setAvailableRooms(8);
        dto.setTotalReservations(50);
        dto.setTotalBills(45);
        dto.setTotalRevenue(1500000.0);

        assertEquals(15, dto.getTotalRooms());
        assertEquals(8, dto.getAvailableRooms());
        assertEquals(50, dto.getTotalReservations());
        assertEquals(45, dto.getTotalBills());
        assertEquals(1500000.0, dto.getTotalRevenue());
    }
}
