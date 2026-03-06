package com.example.hotelreservationsystem.service;

import com.example.hotelreservationsystem.dao.ReservationDAO;
import com.example.hotelreservationsystem.dto.BillDetailsDTO;
import com.example.hotelreservationsystem.dto.DashboardStatsDTO;
import com.example.hotelreservationsystem.dto.ReservationSummaryDTO;
import com.example.hotelreservationsystem.model.Rooms;
import com.example.hotelreservationsystem.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReservationService covering:
 * TC02 – Add Reservation (FR2)
 * TC03 – Overlapping Dates (FR2, A3)
 * TC04 – Input Validation (FR2, NFR3)
 * TC05 – Display Reservation (FR3)
 * TC06 – Billing Calculation (FR4)
 * TC07 – Bill Printing / Display (FR5)
 * Room management and reservation completion
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationDAO reservationDAO;

    @Mock
    private EmailService emailService;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationServiceImpl(reservationDAO, emailService);
    }

    // ========== Dashboard Stats ==========

    @Test
    @DisplayName("getDashboardStats should return stats from DAO")
    void getDashboardStats_shouldReturnStats() {
        DashboardStatsDTO mockStats = new DashboardStatsDTO(10, 5, 20, 15, 50000.0);
        when(reservationDAO.getDashboardStats()).thenReturn(mockStats);

        DashboardStatsDTO result = reservationService.getDashboardStats();

        assertNotNull(result);
        assertEquals(10, result.getTotalRooms());
        assertEquals(5, result.getAvailableRooms());
        assertEquals(20, result.getTotalReservations());
        assertEquals(15, result.getTotalBills());
        assertEquals(50000.0, result.getTotalRevenue());
    }

    // ========== Room Management (A1 – Room Types) ==========

    @Nested
    @DisplayName("Room Management Tests (A1)")
    class RoomManagementTests {

        @Test
        @DisplayName("getAllRooms should return list from DAO")
        void getAllRooms_shouldReturnListFromDAO() {
            List<Rooms> mockRooms = Arrays.asList(
                    new Rooms(1, "R101", "SINGLE", 5000.0, "AVAILABLE", "Standard room", null, null),
                    new Rooms(2, "R201", "DELUXE", 15000.0, "AVAILABLE", "Ocean view", null, null)
            );
            when(reservationDAO.findAllRooms()).thenReturn(mockRooms);

            List<Rooms> result = reservationService.getAllRooms();

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("addRoom should succeed with valid room details")
        void addRoom_shouldSucceed_withValidDetails() {
            when(reservationDAO.createRoom("R101", "SINGLE", 5000.0, "AVAILABLE", "Standard room")).thenReturn(1);

            int roomId = reservationService.addRoom("R101", "SINGLE", 5000.0, "AVAILABLE", "Standard room");

            assertEquals(1, roomId);
        }

        @Test
        @DisplayName("addRoom should throw when room number is blank")
        void addRoom_shouldThrow_whenRoomNumberBlank() {
            assertThrows(IllegalArgumentException.class,
                    () -> reservationService.addRoom("", "SINGLE", 5000.0, "AVAILABLE", "desc"));
        }

        @Test
        @DisplayName("addRoom should throw when room number has special characters")
        void addRoom_shouldThrow_whenRoomNumberInvalid() {
            assertThrows(IllegalArgumentException.class,
                    () -> reservationService.addRoom("R@101", "SINGLE", 5000.0, "AVAILABLE", "desc"));
        }

        @Test
        @DisplayName("addRoom should throw when room type is invalid")
        void addRoom_shouldThrow_whenRoomTypeInvalid() {
            assertThrows(IllegalArgumentException.class,
                    () -> reservationService.addRoom("R101", "PENTHOUSE", 5000.0, "AVAILABLE", "desc"));
        }

        @Test
        @DisplayName("addRoom should throw when room type is blank")
        void addRoom_shouldThrow_whenRoomTypeBlank() {
            assertThrows(IllegalArgumentException.class,
                    () -> reservationService.addRoom("R101", "", 5000.0, "AVAILABLE", "desc"));
        }

        @Test
        @DisplayName("addRoom should throw when rate is zero or negative")
        void addRoom_shouldThrow_whenRateNonPositive() {
            assertThrows(IllegalArgumentException.class,
                    () -> reservationService.addRoom("R101", "SINGLE", 0, "AVAILABLE", "desc"));
            assertThrows(IllegalArgumentException.class,
                    () -> reservationService.addRoom("R101", "SINGLE", -100, "AVAILABLE", "desc"));
        }

        @Test
        @DisplayName("addRoom should throw when status is blank")
        void addRoom_shouldThrow_whenStatusBlank() {
            assertThrows(IllegalArgumentException.class,
                    () -> reservationService.addRoom("R101", "SINGLE", 5000.0, "", "desc"));
        }

        @Test
        @DisplayName("addRoom should throw when status is invalid")
        void addRoom_shouldThrow_whenStatusInvalid() {
            assertThrows(IllegalArgumentException.class,
                    () -> reservationService.addRoom("R101", "SINGLE", 5000.0, "OCCUPIED", "desc"));
        }

        @Test
        @DisplayName("addRoom should accept all valid room types (A1)")
        void addRoom_shouldAcceptAllValidRoomTypes() {
            String[] validTypes = {"SINGLE", "DOUBLE", "DELUXE", "SUITE"};
            for (String type : validTypes) {
                when(reservationDAO.createRoom(eq("R101"), eq(type), eq(5000.0), eq("AVAILABLE"), isNull())).thenReturn(1);
                int id = reservationService.addRoom("R101", type, 5000.0, "AVAILABLE", null);
                assertEquals(1, id);
            }
        }

        @Test
        @DisplayName("getRoomById should return room when valid ID")
        void getRoomById_shouldReturnRoom_whenIdValid() {
            Rooms mockRoom = new Rooms(1, "R101", "SINGLE", 5000.0, "AVAILABLE", "desc", null, null);
            when(reservationDAO.getRoomById(1)).thenReturn(Optional.of(mockRoom));

            Optional<Rooms> result = reservationService.getRoomById(1);

            assertTrue(result.isPresent());
            assertEquals("R101", result.get().getRoomNumber());
        }

        @Test
        @DisplayName("getRoomById should return empty when ID <= 0")
        void getRoomById_shouldReturnEmpty_whenIdInvalid() {
            Optional<Rooms> result = reservationService.getRoomById(0);

            assertTrue(result.isEmpty());
            verify(reservationDAO, never()).getRoomById(anyInt());
        }

        @Test
        @DisplayName("updateRoom should succeed with valid data")
        void updateRoom_shouldSucceed_withValidData() {
            when(reservationDAO.updateRoomWithFacilities(eq(1), eq("R101"), eq("DELUXE"), eq(15000.0), eq("AVAILABLE"), eq("Updated"), isNull())).thenReturn(true);

            boolean result = reservationService.updateRoom(1, "R101", "DELUXE", 15000.0, "AVAILABLE", "Updated");

            assertTrue(result);
        }

        @Test
        @DisplayName("updateRoom should throw when room ID <= 0")
        void updateRoom_shouldThrow_whenRoomIdInvalid() {
            assertThrows(IllegalArgumentException.class,
                    () -> reservationService.updateRoom(0, "R101", "DELUXE", 15000.0, "AVAILABLE", "desc"));
        }

        @Test
        @DisplayName("updateRoom should throw when room not found")
        void updateRoom_shouldThrow_whenRoomNotFound() {
            when(reservationDAO.updateRoomWithFacilities(eq(999), eq("R101"), eq("DELUXE"), eq(15000.0), eq("AVAILABLE"), isNull(), isNull())).thenReturn(false);

            assertThrows(IllegalArgumentException.class,
                    () -> reservationService.updateRoom(999, "R101", "DELUXE", 15000.0, "AVAILABLE", null));
        }

        @Test
        @DisplayName("getAvailableRooms should return list from DAO")
        void getAvailableRooms_shouldReturnFilteredRooms() {
            LocalDate checkIn = LocalDate.now().plusDays(1);
            LocalDate checkOut = LocalDate.now().plusDays(3);
            List<Rooms> mockRooms = Arrays.asList(
                    new Rooms(1, "R101", "DELUXE", 15000.0, "AVAILABLE", null, null, null)
            );
            when(reservationDAO.findAvailableRooms(checkIn, checkOut, "DELUXE")).thenReturn(mockRooms);

            List<Rooms> result = reservationService.getAvailableRooms(checkIn, checkOut, "DELUXE");

            assertEquals(1, result.size());
        }
    }

    // ========== TC02 – Add Reservation (FR2) ==========

    @Nested
    @DisplayName("TC02 – Create Reservation Tests (FR2)")
    class CreateReservationTests {

        @Test
        @DisplayName("TC02.1 – Should create reservation with valid data and return unique ID")
        void createReservation_shouldCallDao_whenRoomIsAvailable() {
            when(reservationDAO.isRoomAvailable(1, LocalDate.now(), LocalDate.now().plusDays(2))).thenReturn(true);
            when(reservationDAO.createReservationWithPrimaryGuest(
                    eq(2), eq("123 Street"), eq("555-1234"), eq("DELUXE"),
                    any(LocalDate.class), any(LocalDate.class),
                    eq(1), eq("John Doe"), eq(30), eq("123456789V"), eq("P123"), eq("john@test.com"), eq("555-1234")
            )).thenReturn(1001L);

            long reservationId = reservationService.createReservation(
                    2, "123 Street", "555-1234", "DELUXE",
                    LocalDate.now(), LocalDate.now().plusDays(2),
                    1, "John Doe", 30, "123456789V", "P123", "john@test.com", "555-1234"
            );

            assertEquals(1001L, reservationId);
            verify(reservationDAO, times(1)).createReservationWithPrimaryGuest(
                    anyInt(), anyString(), anyString(), anyString(),
                    any(), any(), anyInt(), anyString(), anyInt(), anyString(), anyString(), anyString(), anyString()
            );
        }

        @Test
        @DisplayName("TC02.2 – Should generate unique reservation number (A2)")
        void createReservation_shouldReturnUniqueId() {
            when(reservationDAO.isRoomAvailable(anyInt(), any(), any())).thenReturn(true);
            when(reservationDAO.createReservationWithPrimaryGuest(
                    anyInt(), anyString(), anyString(), anyString(),
                    any(), any(), anyInt(), anyString(), any(), anyString(), anyString(), anyString(), anyString()
            )).thenReturn(1001L, 1002L);

            long id1 = reservationService.createReservation(
                    1, "Address 1", "555-0001", "SINGLE",
                    LocalDate.now(), LocalDate.now().plusDays(1),
                    1, "Guest One", 25, "NIC001", "P001", "g1@test.com", "555-0001");

            long id2 = reservationService.createReservation(
                    1, "Address 2", "555-0002", "DOUBLE",
                    LocalDate.now(), LocalDate.now().plusDays(1),
                    2, "Guest Two", 30, "NIC002", "P002", "g2@test.com", "555-0002");

            assertNotEquals(id1, id2);
        }
    }

    // ========== TC03 – Overlapping Dates (FR2, A3) ==========

    @Nested
    @DisplayName("TC03 – Overlapping Dates Tests (FR2, A3)")
    class OverlappingDatesTests {

        @Test
        @DisplayName("TC03.1 – Should reject reservation when room dates overlap (A3)")
        void createReservation_shouldThrowException_whenRoomNotAvailable() {
            when(reservationDAO.isRoomAvailable(1, LocalDate.now(), LocalDate.now().plusDays(2))).thenReturn(false);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                reservationService.createReservation(
                        2, "123 Street", "555-1234", "DELUXE",
                        LocalDate.now(), LocalDate.now().plusDays(2),
                        1, "John Doe", 30, "123456789V", "P123", "john@test.com", "555-1234"
                );
            });

            assertEquals("Selected room is not available for the date range.", exception.getMessage());
        }

        @Test
        @DisplayName("TC03.2 – Should check availability before confirming reservation (A3)")
        void createReservation_shouldCheckAvailabilityFirst() {
            when(reservationDAO.isRoomAvailable(1, LocalDate.now(), LocalDate.now().plusDays(3))).thenReturn(true);
            when(reservationDAO.createReservationWithPrimaryGuest(
                    anyInt(), anyString(), anyString(), anyString(),
                    any(), any(), anyInt(), anyString(), any(), anyString(), anyString(), anyString(), anyString()
            )).thenReturn(1001L);

            reservationService.createReservation(
                    1, "Address", "555-1234", "DELUXE",
                    LocalDate.now(), LocalDate.now().plusDays(3),
                    1, "Guest", 25, "NIC001", "P001", "guest@test.com", "555-1234");

            verify(reservationDAO).isRoomAvailable(1, LocalDate.now(), LocalDate.now().plusDays(3));
        }
    }

    // ========== TC04 – Reservation Input Validation (FR2, NFR3) ==========

    @Nested
    @DisplayName("TC04 – Reservation Input Validation Tests (FR2, NFR3)")
    class ReservationValidationTests {

        @Test
        @DisplayName("TC04.1 – Should throw when guest count is zero")
        void createReservation_shouldThrow_whenGuestCountZero() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    reservationService.createReservation(
                            0, "Address", "555-1234", "DELUXE",
                            LocalDate.now(), LocalDate.now().plusDays(2),
                            1, "Guest", 25, "NIC", "P001", "guest@test.com", "555"));
            assertEquals("Guest count must be at least 1.", ex.getMessage());
        }

        @Test
        @DisplayName("TC04.2 – Should throw when room ID is invalid")
        void createReservation_shouldThrow_whenRoomIdInvalid() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    reservationService.createReservation(
                            1, "Address", "555-1234", "DELUXE",
                            LocalDate.now(), LocalDate.now().plusDays(2),
                            0, "Guest", 25, "NIC", "P001", "guest@test.com", "555"));
            assertEquals("Please select a valid room.", ex.getMessage());
        }

        @Test
        @DisplayName("TC04.3 – Should throw when contact number is blank")
        void createReservation_shouldThrow_whenContactBlank() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    reservationService.createReservation(
                            1, "Address", "", "DELUXE",
                            LocalDate.now(), LocalDate.now().plusDays(2),
                            1, "Guest", 25, "NIC", "P001", "guest@test.com", "555"));
            assertEquals("Contact number is required.", ex.getMessage());
        }

        @Test
        @DisplayName("TC04.4 – Should throw when room type is blank")
        void createReservation_shouldThrow_whenRoomTypeBlank() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    reservationService.createReservation(
                            1, "Address", "555-1234", "",
                            LocalDate.now(), LocalDate.now().plusDays(2),
                            1, "Guest", 25, "NIC", "P001", "guest@test.com", "555"));
            assertEquals("Room type is required.", ex.getMessage());
        }

        @Test
        @DisplayName("TC04.5 – Should throw when check-out not after check-in")
        void createReservation_shouldThrow_whenCheckOutBeforeCheckIn() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    reservationService.createReservation(
                            1, "Address", "555-1234", "DELUXE",
                            LocalDate.now().plusDays(2), LocalDate.now(),
                            1, "Guest", 25, "NIC", "P001", "guest@test.com", "555"));
            assertEquals("Check-out date must be after check-in date.", ex.getMessage());
        }

        @Test
        @DisplayName("TC04.6 – Should throw when check-in equals check-out")
        void createReservation_shouldThrow_whenCheckInEqualsCheckOut() {
            LocalDate sameDate = LocalDate.now();
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    reservationService.createReservation(
                            1, "Address", "555-1234", "DELUXE",
                            sameDate, sameDate,
                            1, "Guest", 25, "NIC", "P001", "guest@test.com", "555"));
            assertEquals("Check-out date must be after check-in date.", ex.getMessage());
        }

        @Test
        @DisplayName("TC04.7 – Should throw when guest full name is blank")
        void createReservation_shouldThrow_whenGuestNameBlank() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    reservationService.createReservation(
                            1, "Address", "555-1234", "DELUXE",
                            LocalDate.now(), LocalDate.now().plusDays(2),
                            1, "", 25, "NIC", "P001", "guest@test.com", "555"));
            assertEquals("Primary guest full name is required.", ex.getMessage());
        }

        @Test
        @DisplayName("TC04.8 – Should throw when guest email is blank")
        void createReservation_shouldThrow_whenEmailBlank() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    reservationService.createReservation(
                            1, "Address", "555-1234", "DELUXE",
                            LocalDate.now(), LocalDate.now().plusDays(2),
                            1, "Guest", 25, "NIC", "P001", "", "555"));
            assertEquals("Primary guest email is required.", ex.getMessage());
        }

        @Test
        @DisplayName("TC04.9 – Should throw when dates are null")
        void createReservation_shouldThrow_whenDatesNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    reservationService.createReservation(
                            1, "Address", "555-1234", "DELUXE",
                            null, null,
                            1, "Guest", 25, "NIC", "P001", "guest@test.com", "555"));
        }
    }

    // ========== TC05 – Display Reservation (FR3) ==========

    @Nested
    @DisplayName("TC05 – Display Reservation Tests (FR3)")
    class DisplayReservationTests {

        @Test
        @DisplayName("TC05.1 – Should display reservation details for valid ID")
        void getReservationById_shouldReturnDetails() {
            ReservationSummaryDTO mockReservation = new ReservationSummaryDTO(
                    1001L, 2, "0771234567", "2026-03-10", "2026-03-13",
                    1, "R101", "DELUXE", 15000.0, "John Doe", "john@test.com", "CONFIRMED");
            when(reservationDAO.findReservationSummaryById(1001L)).thenReturn(Optional.of(mockReservation));

            Optional<ReservationSummaryDTO> result = reservationService.getReservationById(1001L);

            assertTrue(result.isPresent());
            assertEquals("John Doe", result.get().getGuestName());
            assertEquals("R101", result.get().getRoomNumber());
            assertEquals("DELUXE", result.get().getRoomType());
            assertEquals("2026-03-10", result.get().getCheckInDate());
            assertEquals("2026-03-13", result.get().getCheckOutDate());
        }

        @Test
        @DisplayName("TC05.2 – Should return empty for non-existent reservation")
        void getReservationById_shouldReturnEmpty_whenNotFound() {
            when(reservationDAO.findReservationSummaryById(9999L)).thenReturn(Optional.empty());

            Optional<ReservationSummaryDTO> result = reservationService.getReservationById(9999L);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("TC05.3 – Should return all reservations")
        void getReservations_shouldReturnAllReservations() {
            List<ReservationSummaryDTO> mockList = Arrays.asList(
                    new ReservationSummaryDTO(1001L, 2, "555-1234", "2026-03-10", "2026-03-13", 1, "R101", "DELUXE", 15000.0, "John", "j@test.com", "CONFIRMED"),
                    new ReservationSummaryDTO(1002L, 1, "555-5678", "2026-03-15", "2026-03-17", 2, "R201", "SINGLE", 5000.0, "Jane", "ja@test.com", "COMPLETED")
            );
            when(reservationDAO.findAllReservationSummaries()).thenReturn(mockList);

            List<ReservationSummaryDTO> result = reservationService.getReservations();

            assertEquals(2, result.size());
        }
    }

    // ========== TC06 – Billing Calculation (FR4) ==========

    @Nested
    @DisplayName("TC06 – Billing Calculation Tests (FR4)")
    class BillingCalculationTests {

        @Test
        @DisplayName("TC06.1 – Calculate bill with extras and discount")
        void createBill_shouldCalculateCorrectly_withDiscountAndExtras() {
            long reservationId = 1001L;
            ReservationSummaryDTO mockReservation = new ReservationSummaryDTO();
            mockReservation.setCheckInDate(LocalDate.now().toString());
            mockReservation.setCheckOutDate(LocalDate.now().plusDays(3).toString());
            mockReservation.setRatePerNight(5000.0);

            when(reservationDAO.findReservationSummaryById(reservationId)).thenReturn(Optional.of(mockReservation));

            reservationService.createBill(reservationId, 2000.0, 1500.0);

            // Expected: (5000 * 3) + 2000 - 1500 = 15500
            verify(reservationDAO).createOrUpdateBill(
                    eq(reservationId),
                    eq(3),
                    eq(5000.0),
                    eq(2000.0),
                    eq(1500.0),
                    eq(17000.0),
                    eq(15500.0)
            );
        }

        @Test
        @DisplayName("TC06.2 – TC06 Example: Rate=5000, Nights=3, Discount=10%")
        void createBill_shouldCalculateCorrectly_withExampleTC06() {
            long reservationId = 1001L;
            ReservationSummaryDTO mockReservation = new ReservationSummaryDTO();
            mockReservation.setCheckInDate(LocalDate.now().toString());
            mockReservation.setCheckOutDate(LocalDate.now().plusDays(3).toString());
            mockReservation.setRatePerNight(5000.0);

            when(reservationDAO.findReservationSummaryById(reservationId)).thenReturn(Optional.of(mockReservation));

            double subtotal = 5000.0 * 3;
            double discount = subtotal * 0.10;

            reservationService.createBill(reservationId, 0.0, discount);

            // Expected: total = (5000 * 3) - 10% = 13500
            verify(reservationDAO).createOrUpdateBill(
                    eq(reservationId),
                    eq(3),
                    eq(5000.0),
                    eq(0.0),
                    eq(discount),
                    eq(15000.0),
                    eq(13500.0)
            );
        }

        @Test
        @DisplayName("TC06.3 – Bill calculation with zero extras and zero discount")
        void createBill_shouldCalculateCorrectly_withNoExtrasNoDiscount() {
            long reservationId = 1001L;
            ReservationSummaryDTO mockReservation = new ReservationSummaryDTO();
            mockReservation.setCheckInDate(LocalDate.now().toString());
            mockReservation.setCheckOutDate(LocalDate.now().plusDays(5).toString());
            mockReservation.setRatePerNight(8000.0);

            when(reservationDAO.findReservationSummaryById(reservationId)).thenReturn(Optional.of(mockReservation));

            reservationService.createBill(reservationId, 0.0, 0.0);

            // Expected: 8000 * 5 = 40000
            verify(reservationDAO).createOrUpdateBill(
                    eq(reservationId),
                    eq(5),
                    eq(8000.0),
                    eq(0.0),
                    eq(0.0),
                    eq(40000.0),
                    eq(40000.0)
            );
        }

        @Test
        @DisplayName("TC06.4 – Bill calculation with negative extras (should be treated as 0)")
        void createBill_shouldTreatNegativeExtrasAsZero() {
            long reservationId = 1001L;
            ReservationSummaryDTO mockReservation = new ReservationSummaryDTO();
            mockReservation.setCheckInDate(LocalDate.now().toString());
            mockReservation.setCheckOutDate(LocalDate.now().plusDays(2).toString());
            mockReservation.setRatePerNight(5000.0);

            when(reservationDAO.findReservationSummaryById(reservationId)).thenReturn(Optional.of(mockReservation));

            reservationService.createBill(reservationId, -500.0, 0.0);

            verify(reservationDAO).createOrUpdateBill(
                    eq(reservationId),
                    eq(2),
                    eq(5000.0),
                    eq(0.0),
                    eq(0.0),
                    eq(10000.0),
                    eq(10000.0)
            );
        }

        @Test
        @DisplayName("TC06.5 – Bill should not go below zero when discount exceeds subtotal")
        void createBill_shouldNotGoBelowZero_whenDiscountExceedsSubtotal() {
            long reservationId = 1001L;
            ReservationSummaryDTO mockReservation = new ReservationSummaryDTO();
            mockReservation.setCheckInDate(LocalDate.now().toString());
            mockReservation.setCheckOutDate(LocalDate.now().plusDays(1).toString());
            mockReservation.setRatePerNight(1000.0);

            when(reservationDAO.findReservationSummaryById(reservationId)).thenReturn(Optional.of(mockReservation));

            reservationService.createBill(reservationId, 0.0, 5000.0);

            // Expected: total = max(0, 1000 - 5000) = 0
            verify(reservationDAO).createOrUpdateBill(
                    eq(reservationId),
                    eq(1),
                    eq(1000.0),
                    eq(0.0),
                    eq(5000.0),
                    eq(1000.0),
                    eq(0.0)
            );
        }

        @Test
        @DisplayName("TC06.6 – createBill should return empty for non-existent reservation")
        void createBill_shouldReturnEmpty_whenReservationNotFound() {
            when(reservationDAO.findReservationSummaryById(9999L)).thenReturn(Optional.empty());

            Optional<BillDetailsDTO> result = reservationService.createBill(9999L, 0.0, 0.0);

            assertTrue(result.isEmpty());
            verify(reservationDAO, never()).createOrUpdateBill(anyLong(), anyInt(), anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble());
        }

        @Test
        @DisplayName("TC06.7 – Bill with 1 night minimum when same day check-in/out")
        void createBill_shouldUseMinimumOneNight() {
            long reservationId = 1001L;
            ReservationSummaryDTO mockReservation = new ReservationSummaryDTO();
            String sameDate = LocalDate.now().toString();
            mockReservation.setCheckInDate(sameDate);
            mockReservation.setCheckOutDate(sameDate);
            mockReservation.setRatePerNight(5000.0);

            when(reservationDAO.findReservationSummaryById(reservationId)).thenReturn(Optional.of(mockReservation));

            reservationService.createBill(reservationId, 0.0, 0.0);

            verify(reservationDAO).createOrUpdateBill(
                    eq(reservationId),
                    eq(1),
                    eq(5000.0),
                    eq(0.0),
                    eq(0.0),
                    eq(5000.0),
                    eq(5000.0)
            );
        }

        @Test
        @DisplayName("TC06.8 – Bill with extras for room facilities (A9)")
        void createBill_shouldIncludeRoomFacilityExtras() {
            long reservationId = 1001L;
            ReservationSummaryDTO mockReservation = new ReservationSummaryDTO();
            mockReservation.setCheckInDate(LocalDate.now().toString());
            mockReservation.setCheckOutDate(LocalDate.now().plusDays(3).toString());
            mockReservation.setRatePerNight(10000.0);

            when(reservationDAO.findReservationSummaryById(reservationId)).thenReturn(Optional.of(mockReservation));

            // A9: Extras = spa (500/night * 3 = 1500) + breakfast (300/night * 3 = 900) = 2400
            reservationService.createBill(reservationId, 2400.0, 0.0);

            verify(reservationDAO).createOrUpdateBill(
                    eq(reservationId),
                    eq(3),
                    eq(10000.0),
                    eq(2400.0),
                    eq(0.0),
                    eq(32400.0),
                    eq(32400.0)
            );
        }
    }

    // ========== TC07 – Bill Printing / Display (FR5) ==========

    @Nested
    @DisplayName("TC07 – Bill Printing / Display Tests (FR5)")
    class BillPrintingTests {

        @Test
        @DisplayName("TC07.1 – getBillByReservationId should return bill when exists")
        void getBillByReservationId_shouldReturnBill() {
            BillDetailsDTO mockBill = new BillDetailsDTO(
                    1, 1001L, "John Doe", "john@test.com", "R101", "DELUXE",
                    "2026-03-10", "2026-03-13", 3, 5000.0, 0.0, 0.0, 15000.0, 15000.0, "2026-03-05");
            when(reservationDAO.findBillDetailsByReservationId(1001L)).thenReturn(Optional.of(mockBill));

            Optional<BillDetailsDTO> result = reservationService.getBillByReservationId(1001L);

            assertTrue(result.isPresent());
            assertEquals("John Doe", result.get().getGuestName());
            assertEquals(15000.0, result.get().getTotal());
        }

        @Test
        @DisplayName("TC07.2 – getBillByReservationId should return empty when not found")
        void getBillByReservationId_shouldReturnEmpty_whenNotFound() {
            when(reservationDAO.findBillDetailsByReservationId(9999L)).thenReturn(Optional.empty());

            Optional<BillDetailsDTO> result = reservationService.getBillByReservationId(9999L);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("TC07.3 – sendBillToGuest should send email and return true")
        void sendBillToGuest_shouldSendEmail_whenBillExists() {
            BillDetailsDTO mockBill = new BillDetailsDTO(
                    1, 1001L, "John Doe", "john@test.com", "R101", "DELUXE",
                    "2026-03-10", "2026-03-13", 3, 5000.0, 0.0, 0.0, 15000.0, 15000.0, "2026-03-05");
            when(reservationDAO.findBillDetailsByReservationId(1001L)).thenReturn(Optional.of(mockBill));

            boolean result = reservationService.sendBillToGuest(1001L);

            assertTrue(result);
            verify(emailService).sendBillEmail(eq("john@test.com"), anyString());
        }

        @Test
        @DisplayName("TC07.4 – sendBillToGuest should return false when no bill")
        void sendBillToGuest_shouldReturnFalse_whenNoBill() {
            when(reservationDAO.findBillDetailsByReservationId(9999L)).thenReturn(Optional.empty());

            boolean result = reservationService.sendBillToGuest(9999L);

            assertFalse(result);
            verify(emailService, never()).sendBillEmail(anyString(), anyString());
        }

        @Test
        @DisplayName("TC07.5 – sendBillToGuest should return false when no guest email")
        void sendBillToGuest_shouldReturnFalse_whenNoGuestEmail() {
            BillDetailsDTO mockBill = new BillDetailsDTO(
                    1, 1001L, "John Doe", null, "R101", "DELUXE",
                    "2026-03-10", "2026-03-13", 3, 5000.0, 0.0, 0.0, 15000.0, 15000.0, "2026-03-05");
            when(reservationDAO.findBillDetailsByReservationId(1001L)).thenReturn(Optional.of(mockBill));

            boolean result = reservationService.sendBillToGuest(1001L);

            assertFalse(result);
        }

        @Test
        @DisplayName("TC07.6 – sendBillToGuest should return false when guest email is blank")
        void sendBillToGuest_shouldReturnFalse_whenGuestEmailBlank() {
            BillDetailsDTO mockBill = new BillDetailsDTO(
                    1, 1001L, "John Doe", "  ", "R101", "DELUXE",
                    "2026-03-10", "2026-03-13", 3, 5000.0, 0.0, 0.0, 15000.0, 15000.0, "2026-03-05");
            when(reservationDAO.findBillDetailsByReservationId(1001L)).thenReturn(Optional.of(mockBill));

            boolean result = reservationService.sendBillToGuest(1001L);

            assertFalse(result);
        }
    }

    // ========== Reservation Completion & Room Booking Dates ==========

    @Nested
    @DisplayName("Reservation Completion Tests")
    class ReservationCompletionTests {

        @Test
        @DisplayName("completeReservation should update status and release room")
        void completeReservation_shouldUpdateStatusAndReleaseRoom() {
            ReservationSummaryDTO mockReservation = new ReservationSummaryDTO();
            mockReservation.setRoomId(5);
            when(reservationDAO.findReservationSummaryById(1001L)).thenReturn(Optional.of(mockReservation));
            when(reservationDAO.updateReservationStatus(1001L, "COMPLETED")).thenReturn(true);
            when(reservationDAO.updateRoomStatus(5, "AVAILABLE")).thenReturn(true);

            boolean result = reservationService.completeReservation(1001L);

            assertTrue(result);
            verify(reservationDAO).updateReservationStatus(1001L, "COMPLETED");
            verify(reservationDAO).updateRoomStatus(5, "AVAILABLE");
        }

        @Test
        @DisplayName("completeReservation should return false for non-existent reservation")
        void completeReservation_shouldReturnFalse_whenNotFound() {
            when(reservationDAO.findReservationSummaryById(9999L)).thenReturn(Optional.empty());

            boolean result = reservationService.completeReservation(9999L);

            assertFalse(result);
        }

        @Test
        @DisplayName("getRoomBookingDates should return bookings for valid room")
        void getRoomBookingDates_shouldReturnBookings() {
            List<ReservationSummaryDTO> mockBookings = Arrays.asList(
                    new ReservationSummaryDTO(1001L, 2, "555", "2026-03-10", "2026-03-13", 1, "R101", "DELUXE", 15000.0, "John", "j@t.com", "CONFIRMED")
            );
            when(reservationDAO.findUpcomingReservationsByRoom(1)).thenReturn(mockBookings);

            List<ReservationSummaryDTO> result = reservationService.getRoomBookingDates(1);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("getRoomBookingDates should return empty list for invalid room ID")
        void getRoomBookingDates_shouldReturnEmptyList_whenRoomIdInvalid() {
            List<ReservationSummaryDTO> result = reservationService.getRoomBookingDates(0);

            assertTrue(result.isEmpty());
            verify(reservationDAO, never()).findUpcomingReservationsByRoom(anyInt());
        }
    }
}
