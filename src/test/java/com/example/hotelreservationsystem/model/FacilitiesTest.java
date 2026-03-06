package com.example.hotelreservationsystem.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Facilities model entity.
 * Requirement: A9 – Room Facilities & Extras
 */
class FacilitiesTest {

    @Test
    void defaultConstructor_shouldCreateEmptyFacility() {
        Facilities facility = new Facilities();

        assertEquals(0, facility.getId());
        assertNull(facility.getName());
        assertNull(facility.getCategory());
        assertNull(facility.getDescription());
        assertNull(facility.getCreatedAt());
    }

    @Test
    void parameterizedConstructor_shouldSetAllFields() {
        Facilities facility = new Facilities(1, "Breakfast Buffet", "FOOD", "Full breakfast included", "2026-01-01");

        assertEquals(1, facility.getId());
        assertEquals("Breakfast Buffet", facility.getName());
        assertEquals("FOOD", facility.getCategory());
        assertEquals("Full breakfast included", facility.getDescription());
        assertEquals("2026-01-01", facility.getCreatedAt());
    }

    @Test
    void setters_shouldUpdateFields() {
        Facilities facility = new Facilities();
        facility.setId(3);
        facility.setName("Spa Access");
        facility.setCategory("AMENITY");
        facility.setDescription("Access to hotel spa");
        facility.setCreatedAt("2026-02-01");

        assertEquals(3, facility.getId());
        assertEquals("Spa Access", facility.getName());
        assertEquals("AMENITY", facility.getCategory());
        assertEquals("Access to hotel spa", facility.getDescription());
        assertEquals("2026-02-01", facility.getCreatedAt());
    }

    @Test
    void facilityCategories_shouldSupportExpectedTypes() {
        String[] categories = {"FOOD", "AMENITY", "SERVICE", "OTHER"};
        for (String cat : categories) {
            Facilities facility = new Facilities();
            facility.setCategory(cat);
            assertEquals(cat, facility.getCategory());
        }
    }
}
