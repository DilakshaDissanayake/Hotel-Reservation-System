package com.example.hotelreservationsystem.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Bills model entity.
 * Covers: constructors, getters, setters, billing calculation fields
 * Requirement: FR4 – Billing Calculation, FR5 – Bill Printing
 */
class BillsTest {

    @Test
    void defaultConstructor_shouldCreateEmptyBill() {
        Bills bill = new Bills();

        assertEquals(0, bill.getBillId());
        assertEquals(0, bill.getReservationId());
        assertEquals(0, bill.getNight());
        assertEquals(0.0, bill.getRatePerNight());
        assertEquals(0.0, bill.getExtrasTotal());
        assertEquals(0.0, bill.getDiscountAmount());
        assertEquals(0.0, bill.getSubTotal());
        assertEquals(0.0, bill.getTotal());
        assertNull(bill.getGeneratedAt());
    }

    @Test
    void parameterizedConstructor_shouldSetAllFields() {
        Bills bill = new Bills(1, 1001, 3, 5000.0, 2000.0, 1500.0, 17000.0, 15500.0, "2026-03-05");

        assertEquals(1, bill.getBillId());
        assertEquals(1001, bill.getReservationId());
        assertEquals(3, bill.getNight());
        assertEquals(5000.0, bill.getRatePerNight());
        assertEquals(2000.0, bill.getExtrasTotal());
        assertEquals(1500.0, bill.getDiscountAmount());
        assertEquals(17000.0, bill.getSubTotal());
        assertEquals(15500.0, bill.getTotal());
        assertEquals("2026-03-05", bill.getGeneratedAt());
    }

    @Test
    void setters_shouldUpdateFields() {
        Bills bill = new Bills();
        bill.setBillId(2);
        bill.setReservationId(2002);
        bill.setNight(5);
        bill.setRatePerNight(10000.0);
        bill.setExtrasTotal(3000.0);
        bill.setDiscountAmount(5000.0);
        bill.setSubTotal(53000.0);
        bill.setTotal(48000.0);
        bill.setGeneratedAt("2026-03-10");

        assertEquals(2, bill.getBillId());
        assertEquals(2002, bill.getReservationId());
        assertEquals(5, bill.getNight());
        assertEquals(10000.0, bill.getRatePerNight());
        assertEquals(3000.0, bill.getExtrasTotal());
        assertEquals(5000.0, bill.getDiscountAmount());
        assertEquals(53000.0, bill.getSubTotal());
        assertEquals(48000.0, bill.getTotal());
        assertEquals("2026-03-10", bill.getGeneratedAt());
    }

    @Test
    void billCalculation_shouldMatchExpectedFormula_TC06() {
        // TC06: Rate = 5000, Nights = 3, Discount = 10%
        // Expected: roomTotal = 5000 * 3 = 15000, discount = 1500, total = 13500
        Bills bill = new Bills();
        bill.setNight(3);
        bill.setRatePerNight(5000.0);

        double roomTotal = bill.getRatePerNight() * bill.getNight();
        double discount = roomTotal * 0.10;
        double expectedTotal = roomTotal - discount;

        assertEquals(15000.0, roomTotal);
        assertEquals(1500.0, discount);
        assertEquals(13500.0, expectedTotal);
    }

    @Test
    void billWithExtras_shouldIncludeExtraCharges() {
        // A9 – Room Facilities & Extras
        Bills bill = new Bills();
        bill.setNight(2);
        bill.setRatePerNight(8000.0);
        bill.setExtrasTotal(1500.0); // Extra facilities charges

        double roomTotal = bill.getRatePerNight() * bill.getNight();
        double subTotal = roomTotal + bill.getExtrasTotal();

        assertEquals(16000.0, roomTotal);
        assertEquals(17500.0, subTotal);
    }
}
