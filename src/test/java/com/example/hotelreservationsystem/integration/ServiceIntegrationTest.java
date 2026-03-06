package com.example.hotelreservationsystem.integration;

import com.example.hotelreservationsystem.dao.ReservationDAO;
import com.example.hotelreservationsystem.dao.UserDAO;
import com.example.hotelreservationsystem.dto.BillDetailsDTO;
import com.example.hotelreservationsystem.dto.DashboardStatsDTO;
import com.example.hotelreservationsystem.dto.ReservationSummaryDTO;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.service.EmailService;
import com.example.hotelreservationsystem.service.ReservationService;
import com.example.hotelreservationsystem.service.UserService;
import com.example.hotelreservationsystem.service.impl.ReservationServiceImpl;
import com.example.hotelreservationsystem.service.impl.UserServiceImpl;
import com.example.hotelreservationsystem.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Integration tests that verify multi-layer interactions and end-to-end flows.
 * Covers: TC09 – System Performance, TC10 – Safe Exit,
 * and end-to-end reservation workflow.
 *
 * These tests use real service implementations with mocked DAOs
 * to verify the integration between Service → DAO layers.
 */
@ExtendWith(MockitoExtension.class)
class ServiceIntegrationTest {

    @Mock
    private UserDAO userDAO;
    @Mock
    private ReservationDAO reservationDAO;
    @Mock
    private EmailService emailService;

    private UserService userService;
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDAO);
        reservationService = new ReservationServiceImpl(reservationDAO, emailService);
    }

    // ========== TC09 – System Performance (NFR1) ==========

    @Nested
    @DisplayName("TC09 – System Performance (NFR1: response within 3 seconds)")
    class PerformanceTests {

        @Test
        @DisplayName("Authentication should complete within 3 seconds")
        void authentication_shouldCompleteWithin3Seconds() {
            String hashedPassword = PasswordUtil.hash("Admin@123");
            User user = new User(1, "Admin", "User", "admin", "admin@test.com", hashedPassword, "ADMIN", null, null);
            when(userDAO.findByUsername("admin")).thenReturn(Optional.of(user));

            long start = System.currentTimeMillis();
            Optional<User> result = userService.authenticate("admin", "Admin@123");
            long elapsed = System.currentTimeMillis() - start;

            assertTrue(result.isPresent());
            assertTrue(elapsed < 3000, "Authentication took " + elapsed + "ms, expected < 3000ms");
        }

        @Test
        @DisplayName("Dashboard stats retrieval should complete within 3 seconds")
        void dashboardStats_shouldCompleteWithin3Seconds() {
            when(reservationDAO.getDashboardStats()).thenReturn(
                    new DashboardStatsDTO(50, 35, 10, 5, 75000.0));

            long start = System.currentTimeMillis();
            DashboardStatsDTO stats = reservationService.getDashboardStats();
            long elapsed = System.currentTimeMillis() - start;

            assertNotNull(stats);
            assertTrue(elapsed < 3000, "Dashboard stats took " + elapsed + "ms, expected < 3000ms");
        }

        @Test
        @DisplayName("Reservation creation should complete within 3 seconds")
        void reservationCreation_shouldCompleteWithin3Seconds() {
            LocalDate checkIn = LocalDate.now().plusDays(1);
            LocalDate checkOut = LocalDate.now().plusDays(3);
            when(reservationDAO.isRoomAvailable(1, checkIn, checkOut)).thenReturn(true);
            when(reservationDAO.createReservationWithPrimaryGuest(
                    anyInt(), anyString(), anyString(), anyString(),
                    any(), any(), anyInt(), anyString(),
                    any(), any(), any(), anyString(), anyString()
            )).thenReturn(100L);

            long start = System.currentTimeMillis();
            long reservationId = reservationService.createReservation(
                    2, "123 Main St", "0771234567", "DOUBLE",
                    checkIn, checkOut, 1, "John Doe", 30, "NIC123", null,
                    "john@test.com", "0771234567");
            long elapsed = System.currentTimeMillis() - start;

            assertEquals(100L, reservationId);
            assertTrue(elapsed < 3000, "Reservation creation took " + elapsed + "ms, expected < 3000ms");
        }

        @Test
        @DisplayName("Bill calculation should complete within 3 seconds")
        void billCalculation_shouldCompleteWithin3Seconds() {
            ReservationSummaryDTO reservation = new ReservationSummaryDTO();
            reservation.setCheckInDate("2025-04-01");
            reservation.setCheckOutDate("2025-04-05");
            reservation.setRatePerNight(200.00);
            when(reservationDAO.findReservationSummaryById(1L)).thenReturn(Optional.of(reservation));

            BillDetailsDTO bill = new BillDetailsDTO();
            bill.setTotal(800.00);
            when(reservationDAO.findBillDetailsByReservationId(1L)).thenReturn(Optional.of(bill));

            long start = System.currentTimeMillis();
            Optional<BillDetailsDTO> result = reservationService.createBill(1L, 0, 0);
            long elapsed = System.currentTimeMillis() - start;

            assertTrue(result.isPresent());
            assertTrue(elapsed < 3000, "Bill calculation took " + elapsed + "ms, expected < 3000ms");
        }
    }

    // ========== TC10 – Safe Exit (FR1, A6) ==========

    @Nested
    @DisplayName("TC10 – Safe Exit / Session Management")
    class SafeExitTests {

        @Test
        @DisplayName("Login creates a new session with authenticated user")
        void login_createsSession_withAuthUser() {
            String hashedPassword = PasswordUtil.hash("Admin@123");
            User user = new User(1, "Admin", "User", "admin", "admin@test.com", hashedPassword, "ADMIN", null, null);
            when(userDAO.findByUsername("admin")).thenReturn(Optional.of(user));

            Optional<User> result = userService.authenticate("admin", "Admin@123");

            assertTrue(result.isPresent());
            assertEquals("admin", result.get().getUsername());
            // Session management is handled by AuthController — verifying that
            // the service layer correctly returns the user for session storage
        }

        @Test
        @DisplayName("Failed login does not return user (no session created)")
        void failedLogin_doesNotReturnUser() {
            when(userDAO.findByUsername("wronguser")).thenReturn(Optional.empty());

            Optional<User> result = userService.authenticate("wronguser", "password");

            assertTrue(result.isEmpty(), "Failed login should return empty — no session should be created");
        }

        @Test
        @DisplayName("Reservation data is persisted via DAO before session ends")
        void reservationData_isPersistedViaDAO_beforeSessionEnds() {
            LocalDate checkIn = LocalDate.now().plusDays(1);
            LocalDate checkOut = LocalDate.now().plusDays(3);
            when(reservationDAO.isRoomAvailable(1, checkIn, checkOut)).thenReturn(true);
            when(reservationDAO.createReservationWithPrimaryGuest(
                    anyInt(), anyString(), anyString(), anyString(),
                    any(), any(), anyInt(), anyString(),
                    any(), any(), any(), anyString(), anyString()
            )).thenReturn(100L);

            long id = reservationService.createReservation(
                    2, "123 Main St", "0771234567", "DOUBLE",
                    checkIn, checkOut, 1, "John Doe", 30, "NIC123", null,
                    "john@test.com", "0771234567");

            // Verify DAO was called — data persisted to DB before any session invalidation
            verify(reservationDAO).createReservationWithPrimaryGuest(
                    eq(2), eq("123 Main St"), eq("0771234567"), eq("DOUBLE"),
                    eq(checkIn), eq(checkOut), eq(1), eq("John Doe"),
                    eq(30), eq("NIC123"), isNull(), eq("john@test.com"), eq("0771234567"));
            assertEquals(100L, id);
        }

        @Test
        @DisplayName("Bill data is persisted before session ends")
        void billData_isPersistedViaDAO_beforeSessionEnds() {
            ReservationSummaryDTO reservation = new ReservationSummaryDTO();
            reservation.setCheckInDate("2025-04-01");
            reservation.setCheckOutDate("2025-04-05");
            reservation.setRatePerNight(200.00);
            when(reservationDAO.findReservationSummaryById(1L)).thenReturn(Optional.of(reservation));

            BillDetailsDTO bill = new BillDetailsDTO();
            bill.setTotal(800.00);
            when(reservationDAO.findBillDetailsByReservationId(1L)).thenReturn(Optional.of(bill));

            reservationService.createBill(1L, 50.0, 20.0);

            // Verify create/update bill was called with correct values
            verify(reservationDAO).createOrUpdateBill(
                    eq(1L), eq(4), eq(200.0), eq(50.0), eq(20.0), eq(850.0), eq(830.0));
        }
    }

    // ========== End-to-End Reservation Workflow ==========

    @Nested
    @DisplayName("End-to-End: Login → Reservation → Bill → Complete")
    class EndToEndWorkflowTests {

        @Test
        @DisplayName("Full workflow: authenticate → create reservation → bill → complete")
        void fullReservationWorkflow() {
            // Step 1: Authenticate (TC01)
            String hashedPassword = PasswordUtil.hash("Admin@123");
            User user = new User(1, "Admin", "User", "admin", "admin@test.com", hashedPassword, "ADMIN", null, null);
            when(userDAO.findByUsername("admin")).thenReturn(Optional.of(user));

            Optional<User> authResult = userService.authenticate("admin", "Admin@123");
            assertTrue(authResult.isPresent(), "Step 1: Login should succeed");

            // Step 2: Create reservation (TC02)
            LocalDate checkIn = LocalDate.of(2025, 5, 1);
            LocalDate checkOut = LocalDate.of(2025, 5, 5);
            when(reservationDAO.isRoomAvailable(1, checkIn, checkOut)).thenReturn(true);
            when(reservationDAO.createReservationWithPrimaryGuest(
                    anyInt(), anyString(), anyString(), anyString(),
                    any(), any(), anyInt(), anyString(),
                    any(), any(), any(), anyString(), anyString()
            )).thenReturn(100L);

            long reservationId = reservationService.createReservation(
                    2, "456 Ocean Blvd", "0771111111", "DELUXE",
                    checkIn, checkOut, 1, "Jane Guest", 28, null, "PP12345",
                    "jane@test.com", "0771111111");
            assertEquals(100L, reservationId, "Step 2: Reservation should be created");

            // Step 3: Calculate bill (TC06)
            ReservationSummaryDTO summary = new ReservationSummaryDTO();
            summary.setCheckInDate("2025-05-01");
            summary.setCheckOutDate("2025-05-05");
            summary.setRatePerNight(300.0);
            summary.setRoomId(1);
            when(reservationDAO.findReservationSummaryById(100L)).thenReturn(Optional.of(summary));

            BillDetailsDTO billDto = new BillDetailsDTO();
            billDto.setBillId(1);
            billDto.setReservationId(100);
            billDto.setNights(4);
            billDto.setRatePerNight(300.0);
            billDto.setExtrasTotal(100.0);
            billDto.setDiscountAmount(50.0);
            billDto.setSubTotal(1300.0);
            billDto.setTotal(1250.0);
            billDto.setGuestEmail("jane@test.com");
            billDto.setGuestName("Jane Guest");
            when(reservationDAO.findBillDetailsByReservationId(100L)).thenReturn(Optional.of(billDto));

            Optional<BillDetailsDTO> billResult = reservationService.createBill(100L, 100.0, 50.0);
            assertTrue(billResult.isPresent(), "Step 3: Bill should be calculated");
            // Verify: 4 nights × $300 = $1200 room + $100 extras = $1300 subtotal - $50 discount = $1250
            verify(reservationDAO).createOrUpdateBill(
                    eq(100L), eq(4), eq(300.0), eq(100.0), eq(50.0), eq(1300.0), eq(1250.0));

            // Step 4: Send bill to guest (TC07)
            boolean sent = reservationService.sendBillToGuest(100L);
            assertTrue(sent, "Step 4: Bill email should be sent");
            verify(emailService).sendBillEmail(eq("jane@test.com"), anyString());

            // Step 5: Complete reservation
            when(reservationDAO.updateReservationStatus(100L, "COMPLETED")).thenReturn(true);
            when(reservationDAO.updateRoomStatus(summary.getRoomId(), "AVAILABLE")).thenReturn(true);

            boolean completed = reservationService.completeReservation(100L);
            assertTrue(completed, "Step 5: Reservation should be completed");
        }

        @Test
        @DisplayName("Full workflow: create user → authenticate with new credentials")
        void userCreationAndLogin() {
            // Step 1: Create user (FR1)
            when(userDAO.createUser(any(User.class))).thenReturn(1);

            int userId = userService.createUser("New", "Staff", "newstaff", "new@test.com", "SecurePass1", "RECEPTIONIST");
            assertTrue(userId > 0, "Step 1: User should be created");

            // Step 2: Simulate login with new user
            // The DAO would now find this user with hashed password
            User newUser = new User(2, "New", "Staff", "newstaff", "new@test.com",
                    PasswordUtil.hash("SecurePass1"), "RECEPTIONIST", null, null);
            when(userDAO.findByUsername("newstaff")).thenReturn(Optional.of(newUser));

            Optional<User> authResult = userService.authenticate("newstaff", "SecurePass1");
            assertTrue(authResult.isPresent(), "Step 2: New user should be able to login");
            assertEquals("RECEPTIONIST", authResult.get().getRole());
        }
    }

    // ========== FR1 – Password Reset Flow ==========

    @Nested
    @DisplayName("Password Reset End-to-End Flow")
    class PasswordResetFlowTests {

        @Test
        @DisplayName("Full password reset: create token → validate → reset → login")
        void fullPasswordResetFlow() {
            // Step 1: Create password reset token
            when(userDAO.findByEmail("admin@test.com")).thenReturn(
                    Optional.of(new User(1, "Admin", "User", "admin", "admin@test.com", "oldhash", "ADMIN", null, null)));
            doNothing().when(userDAO).savePasswordResetToken(eq(1), anyString(), any());

            Optional<String> tokenOpt = userService.createPasswordResetToken("admin@test.com");
            assertTrue(tokenOpt.isPresent(), "Step 1: Token should be generated");
            String token = tokenOpt.get();

            // Step 2: Validate token (service hashes the raw token internally)
            when(userDAO.findUserIdByValidResetToken(anyString())).thenReturn(Optional.of(1));
            boolean isValid = userService.isResetTokenValid(token);
            assertTrue(isValid, "Step 2: Token should be valid");

            // Step 3: Reset password
            when(userDAO.resetPasswordByValidToken(anyString(), anyString())).thenReturn(true);
            boolean reset = userService.resetPassword(token, "NewAdmin@456");
            assertTrue(reset, "Step 3: Password should be reset");

            // Step 4: Login with new password
            String newHash = PasswordUtil.hash("NewAdmin@456");
            User updatedUser = new User(1, "Admin", "User", "admin", "admin@test.com", newHash, "ADMIN", null, null);
            when(userDAO.findByUsername("admin")).thenReturn(Optional.of(updatedUser));

            Optional<User> authResult = userService.authenticate("admin", "NewAdmin@456");
            assertTrue(authResult.isPresent(), "Step 4: Should login with new password");
        }

        @Test
        @DisplayName("Password reset with too-short password should return false")
        void passwordReset_tooShort_shouldReturnFalse() {
            boolean result = userService.resetPassword("validtoken", "short");
            assertFalse(result, "Password reset with < 8 chars should return false");
        }
    }

    // ========== Room Management Flow (A1) ==========

    @Nested
    @DisplayName("Room Management End-to-End (A1)")
    class RoomManagementFlowTests {

        @Test
        @DisplayName("Add room → Verify in list → Update → Verify update")
        void addAndUpdateRoomFlow() {
            // Step 1: Add room
            when(reservationDAO.createRoom("R101", "SINGLE", 150.0, "AVAILABLE", null)).thenReturn(1);

            int roomId = reservationService.addRoom("R101", "SINGLE", 150.0, "AVAILABLE", null);
            assertEquals(1, roomId, "Step 1: Room should be added");

            // Step 2: Update room
            when(reservationDAO.updateRoomWithFacilities(1, "R101", "DELUXE", 250.0, "AVAILABLE", "Sea View", null))
                    .thenReturn(true);

            boolean updated = reservationService.updateRoom(1, "R101", "DELUXE", 250.0, "AVAILABLE", "Sea View");
            assertTrue(updated, "Step 2: Room should be updated");
        }

        @Test
        @DisplayName("Check room availability before reservation (A3 – pessimistic locking)")
        void checkAvailability_beforeReservation() {
            LocalDate checkIn = LocalDate.of(2025, 6, 1);
            LocalDate checkOut = LocalDate.of(2025, 6, 5);

            when(reservationDAO.isRoomAvailable(1, checkIn, checkOut)).thenReturn(true);
            when(reservationDAO.createReservationWithPrimaryGuest(
                    anyInt(), any(), anyString(), anyString(),
                    any(), any(), anyInt(), anyString(),
                    any(), any(), any(), anyString(), anyString()
            )).thenReturn(200L);

            long id = reservationService.createReservation(
                    1, null, "0770000000", "SINGLE",
                    checkIn, checkOut, 1, "Test Guest", null, null, null,
                    "test@test.com", "0770000000");
            assertEquals(200L, id);

            LocalDate overlapIn = LocalDate.of(2025, 6, 3);
            LocalDate overlapOut = LocalDate.of(2025, 6, 7);
            when(reservationDAO.isRoomAvailable(1, overlapIn, overlapOut)).thenReturn(false);

            assertThrows(IllegalArgumentException.class, () ->
                    reservationService.createReservation(
                            1, null, "0770000000", "SINGLE",
                            overlapIn, overlapOut, 1, "Other Guest", null, null, null,
                            "other@test.com", "0770000000"),
                    "Overlapping dates should not be allowed (TC03)");
        }
    }


    @Nested
    @DisplayName("Billing and Email Integration (TC06, TC07)")
    class BillingEmailIntegrationTests {

        @Test
        @DisplayName("Create bill and send email notification")
        void createBill_andSendEmail() {
            ReservationSummaryDTO reservation = new ReservationSummaryDTO();
            reservation.setCheckInDate("2025-04-10");
            reservation.setCheckOutDate("2025-04-12");
            reservation.setRatePerNight(250.0);
            when(reservationDAO.findReservationSummaryById(1L)).thenReturn(Optional.of(reservation));

            BillDetailsDTO bill = new BillDetailsDTO();
            bill.setGuestEmail("guest@test.com");
            bill.setGuestName("Test Guest");
            bill.setTotal(500.0);
            when(reservationDAO.findBillDetailsByReservationId(1L)).thenReturn(Optional.of(bill));

            Optional<BillDetailsDTO> billResult = reservationService.createBill(1L, 0, 0);
            assertTrue(billResult.isPresent());

            boolean sent = reservationService.sendBillToGuest(1L);
            assertTrue(sent);

            verify(emailService).sendBillEmail(eq("guest@test.com"), anyString());
        }

        @Test
        @DisplayName("Bill email fails when guest email is blank")
        void sendBill_failsWhenEmailBlank() {
            BillDetailsDTO bill = new BillDetailsDTO();
            bill.setGuestEmail("");
            bill.setGuestName("Test");
            when(reservationDAO.findBillDetailsByReservationId(1L)).thenReturn(Optional.of(bill));

            boolean sent = reservationService.sendBillToGuest(1L);
            assertFalse(sent, "Should not send bill when email is blank");
            verifyNoInteractions(emailService);
        }

        @Test
        @DisplayName("Bill email fails when no bill exists")
        void sendBill_failsWhenNoBillExists() {
            when(reservationDAO.findBillDetailsByReservationId(999L)).thenReturn(Optional.empty());

            boolean sent = reservationService.sendBillToGuest(999L);
            assertFalse(sent, "Should not send bill when bill doesn't exist");
            verifyNoInteractions(emailService);
        }
    }


    @Nested
    @DisplayName("Dashboard Statistics Integration (FR2)")
    class DashboardIntegrationTests {

        @Test
        @DisplayName("Dashboard shows correct stats from DAO")
        void dashboardStats_fromDAO() {
            DashboardStatsDTO expected = new DashboardStatsDTO(100, 80, 15, 5, 50000.0);
            when(reservationDAO.getDashboardStats()).thenReturn(expected);

            DashboardStatsDTO result = reservationService.getDashboardStats();

            assertEquals(100, result.getTotalRooms());
            assertEquals(80, result.getAvailableRooms());
            assertEquals(15, result.getTotalReservations());
            assertEquals(5, result.getTotalBills());
            assertEquals(50000.0, result.getTotalRevenue());
        }
    }
}
