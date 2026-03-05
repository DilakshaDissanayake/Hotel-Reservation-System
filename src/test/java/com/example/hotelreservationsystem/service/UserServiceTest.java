package com.example.hotelreservationsystem.service;

import com.example.hotelreservationsystem.dao.UserDAO;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.service.impl.UserServiceImpl;
import com.example.hotelreservationsystem.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService covering:
 * TC01 – Login Authentication (FR1)
 * TC08 – Password Security (NFR4)
 * TC04 – Input Validation (NFR3)
 * User creation and password reset flows
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDAO);
    }

    // ========== TC01 – Login Authentication (FR1) ==========

    @Nested
    @DisplayName("TC01 – Authentication Tests (FR1)")
    class AuthenticationTests {

        @Test
        @DisplayName("TC01.1 – Valid credentials should return authenticated user")
        void authenticate_shouldReturnUser_whenCredentialsValid() {
            String rawPassword = "password123";
            String hash = PasswordUtil.hash(rawPassword);

            User mockUser = new User(1, "John", "Doe", "johndoe", "john@example.com", hash, "ADMIN", null, null);
            when(userDAO.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));

            Optional<User> result = userService.authenticate("johndoe", rawPassword);

            assertTrue(result.isPresent());
            assertEquals("johndoe", result.get().getUsername());
        }

        @Test
        @DisplayName("TC01.2 – Wrong password should return empty (rejected)")
        void authenticate_shouldReturnEmpty_whenPasswordInvalid() {
            String hash = PasswordUtil.hash("password123");

            User mockUser = new User(1, "John", "Doe", "johndoe", "john@example.com", hash, "ADMIN", null, null);
            when(userDAO.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));

            Optional<User> result = userService.authenticate("johndoe", "wrongpass");

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("TC01.3 – Non-existent username should return empty")
        void authenticate_shouldReturnEmpty_whenUsernameNotFound() {
            when(userDAO.findByUsername("nonexistent")).thenReturn(Optional.empty());

            Optional<User> result = userService.authenticate("nonexistent", "password123");

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("TC01.4 – Null username should return empty")
        void authenticate_shouldReturnEmpty_whenUsernameNull() {
            Optional<User> result = userService.authenticate(null, "password123");

            assertTrue(result.isEmpty());
            verify(userDAO, never()).findByUsername(anyString());
        }

        @Test
        @DisplayName("TC01.5 – Blank username should return empty")
        void authenticate_shouldReturnEmpty_whenUsernameBlank() {
            Optional<User> result = userService.authenticate("   ", "password123");

            assertTrue(result.isEmpty());
            verify(userDAO, never()).findByUsername(anyString());
        }

        @Test
        @DisplayName("TC01.6 – Null password should return empty")
        void authenticate_shouldReturnEmpty_whenPasswordNull() {
            Optional<User> result = userService.authenticate("johndoe", null);

            assertTrue(result.isEmpty());
            verify(userDAO, never()).findByUsername(anyString());
        }

        @Test
        @DisplayName("TC01.7 – Blank password should return empty")
        void authenticate_shouldReturnEmpty_whenPasswordBlank() {
            Optional<User> result = userService.authenticate("johndoe", "   ");

            assertTrue(result.isEmpty());
            verify(userDAO, never()).findByUsername(anyString());
        }

        @Test
        @DisplayName("TC01.8 – Username with whitespace should be trimmed")
        void authenticate_shouldTrimUsername() {
            String rawPassword = "password123";
            String hash = PasswordUtil.hash(rawPassword);

            User mockUser = new User(1, "John", "Doe", "johndoe", "john@example.com", hash, "ADMIN", null, null);
            when(userDAO.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));

            Optional<User> result = userService.authenticate("  johndoe  ", rawPassword);

            assertTrue(result.isPresent());
            verify(userDAO).findByUsername("johndoe");
        }
    }

    // ========== TC08 – Password Security (NFR4) ==========

    @Nested
    @DisplayName("TC08 – Password Security Tests (NFR4)")
    class PasswordSecurityTests {

        @Test
        @DisplayName("TC08.1 – Password should be stored as bcrypt hash, not plain text")
        void createUser_shouldStorePasswordAsHash() {
            when(userDAO.createUser(any(User.class))).thenReturn(1);

            userService.createUser("John", "Doe", "johndoe", "john@example.com", "password123", "ADMIN");

            verify(userDAO).createUser(argThat(user -> {
                String storedHash = user.getPasswordHash();
                assertNotEquals("password123", storedHash);
                assertTrue(storedHash.startsWith("$2a$"));
                assertTrue(PasswordUtil.verify("password123", storedHash));
                return true;
            }));
        }

        @Test
        @DisplayName("TC08.2 – Different users with same password should have different hashes")
        void createUser_shouldGenerateUniqueSalts() {
            final String[] capturedHashes = new String[2];

            when(userDAO.createUser(any(User.class))).thenAnswer(invocation -> {
                User u = invocation.getArgument(0);
                if (capturedHashes[0] == null) {
                    capturedHashes[0] = u.getPasswordHash();
                } else {
                    capturedHashes[1] = u.getPasswordHash();
                }
                return 1;
            });

            userService.createUser("John", "Doe", "johndoe", "john@example.com", "samePassword1", "ADMIN");
            userService.createUser("Jane", "Smith", "janesmith", "jane@example.com", "samePassword1", "RECEPTIONIST");

            assertNotEquals(capturedHashes[0], capturedHashes[1]);
        }
    }

    // ========== Password Reset Tests ==========

    @Nested
    @DisplayName("Password Reset Tests")
    class PasswordResetTests {

        @Test
        @DisplayName("createPasswordResetToken should return token for valid email")
        void createPasswordResetToken_shouldReturnToken_whenEmailValid() {
            User mockUser = new User(1, "John", "Doe", "johndoe", "john@example.com", "hash", "ADMIN", null, null);
            when(userDAO.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));

            Optional<String> token = userService.createPasswordResetToken("john@example.com");

            assertTrue(token.isPresent());
            assertFalse(token.get().isEmpty());
            verify(userDAO).savePasswordResetToken(eq(1), anyString(), any(Timestamp.class));
        }

        @Test
        @DisplayName("createPasswordResetToken should return empty for null email")
        void createPasswordResetToken_shouldReturnEmpty_whenEmailNull() {
            Optional<String> token = userService.createPasswordResetToken(null);

            assertTrue(token.isEmpty());
        }

        @Test
        @DisplayName("createPasswordResetToken should return empty for blank email")
        void createPasswordResetToken_shouldReturnEmpty_whenEmailBlank() {
            Optional<String> token = userService.createPasswordResetToken("   ");

            assertTrue(token.isEmpty());
        }

        @Test
        @DisplayName("createPasswordResetToken should return empty for non-existent email")
        void createPasswordResetToken_shouldReturnEmpty_whenEmailNotFound() {
            when(userDAO.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

            Optional<String> token = userService.createPasswordResetToken("unknown@example.com");

            assertTrue(token.isEmpty());
        }

        @Test
        @DisplayName("isResetTokenValid should return true for valid token")
        void isResetTokenValid_shouldReturnTrue_whenTokenValid() {
            when(userDAO.findUserIdByValidResetToken(anyString())).thenReturn(Optional.of(1));

            assertTrue(userService.isResetTokenValid("valid-token"));
        }

        @Test
        @DisplayName("isResetTokenValid should return false for null token")
        void isResetTokenValid_shouldReturnFalse_whenTokenNull() {
            assertFalse(userService.isResetTokenValid(null));
        }

        @Test
        @DisplayName("isResetTokenValid should return false for blank token")
        void isResetTokenValid_shouldReturnFalse_whenTokenBlank() {
            assertFalse(userService.isResetTokenValid("   "));
        }

        @Test
        @DisplayName("resetPassword should return true on success")
        void resetPassword_shouldReturnTrue_whenSuccessful() {
            when(userDAO.resetPasswordByValidToken(anyString(), anyString())).thenReturn(true);

            assertTrue(userService.resetPassword("valid-token", "newPassword123"));
        }

        @Test
        @DisplayName("resetPassword should return false when token is null")
        void resetPassword_shouldReturnFalse_whenTokenNull() {
            assertFalse(userService.resetPassword(null, "newPassword123"));
        }

        @Test
        @DisplayName("resetPassword should return false when password too short")
        void resetPassword_shouldReturnFalse_whenPasswordTooShort() {
            assertFalse(userService.resetPassword("valid-token", "short"));
        }

        @Test
        @DisplayName("resetPassword should return false when password is null")
        void resetPassword_shouldReturnFalse_whenPasswordNull() {
            assertFalse(userService.resetPassword("valid-token", null));
        }
    }

    // ========== TC04 – Input Validation / Create User (FR2, NFR3) ==========

    @Nested
    @DisplayName("TC04 – Create User Validation Tests (NFR3)")
    class CreateUserValidationTests {

        @Test
        @DisplayName("TC04.1 – Should create user with valid data")
        void createUser_shouldSucceed_whenAllFieldsValid() {
            when(userDAO.createUser(any(User.class))).thenReturn(1);

            int id = userService.createUser("John", "Doe", "johndoe", "john@example.com", "password123", "ADMIN");

            assertEquals(1, id);
            verify(userDAO).createUser(any(User.class));
        }

        @Test
        @DisplayName("TC04.2 – Should throw when first name is blank")
        void createUser_shouldThrow_whenFirstNameBlank() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> userService.createUser("", "Doe", "johndoe", "john@example.com", "password123", "ADMIN"));
            assertEquals("First name is required.", ex.getMessage());
        }

        @Test
        @DisplayName("TC04.3 – Should throw when last name is null")
        void createUser_shouldThrow_whenLastNameNull() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> userService.createUser("John", null, "johndoe", "john@example.com", "password123", "ADMIN"));
            assertEquals("Last name is required.", ex.getMessage());
        }

        @Test
        @DisplayName("TC04.4 – Should throw when username is blank")
        void createUser_shouldThrow_whenUsernameBlank() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> userService.createUser("John", "Doe", "  ", "john@example.com", "password123", "ADMIN"));
            assertEquals("Username is required.", ex.getMessage());
        }

        @Test
        @DisplayName("TC04.5 – Should throw when email is invalid (no @)")
        void createUser_shouldThrow_whenEmailInvalid() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> userService.createUser("John", "Doe", "johndoe", "invalidemail", "password123", "ADMIN"));
            assertEquals("Valid email is required.", ex.getMessage());
        }

        @Test
        @DisplayName("TC04.6 – Should throw when email is blank")
        void createUser_shouldThrow_whenEmailBlank() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> userService.createUser("John", "Doe", "johndoe", "", "password123", "ADMIN"));
            assertEquals("Valid email is required.", ex.getMessage());
        }

        @Test
        @DisplayName("TC04.7 – Should throw when password < 8 characters")
        void createUser_shouldThrow_whenPasswordTooShort() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> userService.createUser("John", "Doe", "johndoe", "john@example.com", "short", "ADMIN"));
            assertEquals("Password must be at least 8 characters.", ex.getMessage());
        }

        @Test
        @DisplayName("TC04.8 – Should throw when password is null")
        void createUser_shouldThrow_whenPasswordNull() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> userService.createUser("John", "Doe", "johndoe", "john@example.com", null, "ADMIN"));
            assertEquals("Password must be at least 8 characters.", ex.getMessage());
        }

        @Test
        @DisplayName("TC04.9 – Should throw when role is blank")
        void createUser_shouldThrow_whenRoleBlank() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> userService.createUser("John", "Doe", "johndoe", "john@example.com", "password123", ""));
            assertEquals("Role is required.", ex.getMessage());
        }

        @Test
        @DisplayName("TC04.10 – Role should be stored in uppercase")
        void createUser_shouldStoreRoleUppercase() {
            when(userDAO.createUser(any(User.class))).thenReturn(1);

            userService.createUser("John", "Doe", "johndoe", "john@example.com", "password123", "admin");

            verify(userDAO).createUser(argThat(user -> "ADMIN".equals(user.getRole())));
        }
    }

    // ========== Get All Users ==========

    @Nested
    @DisplayName("Get All Users Tests")
    class GetAllUsersTests {

        @Test
        @DisplayName("getAllUsers should return list from DAO")
        void getAllUsers_shouldReturnListFromDAO() {
            List<User> mockUsers = Arrays.asList(
                    new User(1, "John", "Doe", "johndoe", "john@example.com", "hash", "ADMIN", null, null),
                    new User(2, "Jane", "Smith", "janesmith", "jane@example.com", "hash", "RECEPTIONIST", null, null)
            );
            when(userDAO.findAll()).thenReturn(mockUsers);

            List<User> result = userService.getAllUsers();

            assertEquals(2, result.size());
            assertEquals("johndoe", result.get(0).getUsername());
            assertEquals("janesmith", result.get(1).getUsername());
        }

        @Test
        @DisplayName("getAllUsers should return empty list when no users")
        void getAllUsers_shouldReturnEmptyList_whenNoUsers() {
            when(userDAO.findAll()).thenReturn(Arrays.asList());

            List<User> result = userService.getAllUsers();

            assertTrue(result.isEmpty());
        }
    }
}