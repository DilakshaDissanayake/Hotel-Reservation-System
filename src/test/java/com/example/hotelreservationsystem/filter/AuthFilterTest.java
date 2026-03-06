package com.example.hotelreservationsystem.filter;

import com.example.hotelreservationsystem.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthFilter – access control and role-based authorization.
 * Requirement: FR1 – User Authentication, NFR4 – Security
 * A4 – User Roles (Admin/Receptionist)
 */
@ExtendWith(MockitoExtension.class)
class AuthFilterTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;
    @Mock
    private HttpSession session;

    private AuthFilter authFilter;

    @BeforeEach
    void setUp() {
        authFilter = new AuthFilter();
    }

    private void setupRequestPath(String path) {
        when(request.getRequestURI()).thenReturn("/app" + path);
        when(request.getContextPath()).thenReturn("/app");
    }

    @Nested
    @DisplayName("Public Path Access Tests")
    class PublicPathTests {

        @Test
        @DisplayName("Login page should be accessible without authentication")
        void loginPage_shouldBeAccessible_withoutAuth() throws Exception {
            setupRequestPath("/login");
            when(request.getSession(false)).thenReturn(null);

            authFilter.doFilter(request, response, chain);

            verify(chain).doFilter(request, response);
        }

        @Test
        @DisplayName("Forgot password should be accessible without authentication")
        void forgotPassword_shouldBeAccessible_withoutAuth() throws Exception {
            setupRequestPath("/forgot-password");
            when(request.getSession(false)).thenReturn(null);

            authFilter.doFilter(request, response, chain);

            verify(chain).doFilter(request, response);
        }

        @Test
        @DisplayName("Reset password should be accessible without authentication")
        void resetPassword_shouldBeAccessible_withoutAuth() throws Exception {
            setupRequestPath("/reset-password");
            when(request.getSession(false)).thenReturn(null);

            authFilter.doFilter(request, response, chain);

            verify(chain).doFilter(request, response);
        }

        @Test
        @DisplayName("Static CSS assets should be accessible without authentication")
        void cssAssets_shouldBeAccessible_withoutAuth() throws Exception {
            setupRequestPath("/assets/css/app.css");
            when(request.getSession(false)).thenReturn(null);

            authFilter.doFilter(request, response, chain);

            verify(chain).doFilter(request, response);
        }

        @Test
        @DisplayName("Static JS assets should be accessible without authentication")
        void jsAssets_shouldBeAccessible_withoutAuth() throws Exception {
            setupRequestPath("/assets/js/app.js");
            when(request.getSession(false)).thenReturn(null);

            authFilter.doFilter(request, response, chain);

            verify(chain).doFilter(request, response);
        }

        @Test
        @DisplayName("Image assets should be accessible without authentication")
        void imageAssets_shouldBeAccessible_withoutAuth() throws Exception {
            setupRequestPath("/assets/img/logo.png");
            when(request.getSession(false)).thenReturn(null);

            authFilter.doFilter(request, response, chain);

            verify(chain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("Authentication Required Tests")
    class AuthRequiredTests {

        @Test
        @DisplayName("Unauthenticated user should be redirected to login")
        void unauthenticatedUser_shouldRedirectToLogin() throws Exception {
            setupRequestPath("/dashboard");
            when(request.getSession(false)).thenReturn(null);

            authFilter.doFilter(request, response, chain);

            verify(response).sendRedirect("/app/login");
            verify(chain, never()).doFilter(request, response);
        }

        @Test
        @DisplayName("Unauthenticated user accessing reservations should be redirected")
        void unauthenticatedUser_accessingReservations_shouldRedirect() throws Exception {
            setupRequestPath("/reservations");
            when(request.getSession(false)).thenReturn(null);

            authFilter.doFilter(request, response, chain);

            verify(response).sendRedirect("/app/login");
        }

        @Test
        @DisplayName("Authenticated user should proceed to requested page")
        void authenticatedUser_shouldProceed() throws Exception {
            setupRequestPath("/dashboard");
            User authUser = new User(1, "John", "Doe", "johndoe", "john@test.com", "hash", "RECEPTIONIST", null, null);
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute("authUser")).thenReturn(authUser);
            when(request.getMethod()).thenReturn("GET");

            authFilter.doFilter(request, response, chain);

            verify(chain).doFilter(request, response);
        }

        @Test
        @DisplayName("Session without authUser should redirect to login")
        void sessionWithoutAuthUser_shouldRedirect() throws Exception {
            setupRequestPath("/dashboard");
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute("authUser")).thenReturn(null);

            authFilter.doFilter(request, response, chain);

            verify(response).sendRedirect("/app/login");
        }
    }

    @Nested
    @DisplayName("Role-Based Access Control Tests (A4)")
    class RoleBasedAccessTests {

        @Test
        @DisplayName("Admin should access staff management page")
        void admin_shouldAccessStaffPage() throws Exception {
            setupRequestPath("/staff");
            User admin = new User(1, "Admin", "User", "admin", "admin@test.com", "hash", "ADMIN", null, null);
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute("authUser")).thenReturn(admin);
            when(request.getMethod()).thenReturn("GET");

            authFilter.doFilter(request, response, chain);

            verify(chain).doFilter(request, response);
        }

        @Test
        @DisplayName("Receptionist should be denied access to staff management")
        void receptionist_shouldBeDeniedAccessToStaffPage() throws Exception {
            setupRequestPath("/staff");
            User receptionist = new User(2, "Jane", "Smith", "reception", "reception@test.com", "hash", "RECEPTIONIST", null, null);
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute("authUser")).thenReturn(receptionist);
            when(request.getMethod()).thenReturn("GET");

            authFilter.doFilter(request, response, chain);

            verify(response).sendRedirect("/app/dashboard?error=Access%20Denied");
            verify(chain, never()).doFilter(request, response);
        }

        @Test
        @DisplayName("Admin should access room edit page")
        void admin_shouldAccessRoomEditPage() throws Exception {
            setupRequestPath("/rooms/edit");
            User admin = new User(1, "Admin", "User", "admin", "admin@test.com", "hash", "ADMIN", null, null);
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute("authUser")).thenReturn(admin);
            when(request.getMethod()).thenReturn("GET");

            authFilter.doFilter(request, response, chain);

            verify(chain).doFilter(request, response);
        }

        @Test
        @DisplayName("Receptionist should be denied access to room edit")
        void receptionist_shouldBeDeniedAccessToRoomEdit() throws Exception {
            setupRequestPath("/rooms/edit");
            User receptionist = new User(2, "Jane", "Smith", "reception", "reception@test.com", "hash", "RECEPTIONIST", null, null);
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute("authUser")).thenReturn(receptionist);
            when(request.getMethod()).thenReturn("GET");

            authFilter.doFilter(request, response, chain);

            verify(response).sendRedirect("/app/dashboard?error=Access%20Denied");
        }

        @Test
        @DisplayName("Admin should be able to POST to rooms (add room)")
        void admin_shouldPostToRooms() throws Exception {
            setupRequestPath("/rooms");
            User admin = new User(1, "Admin", "User", "admin", "admin@test.com", "hash", "ADMIN", null, null);
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute("authUser")).thenReturn(admin);
            when(request.getMethod()).thenReturn("POST");

            authFilter.doFilter(request, response, chain);

            verify(chain).doFilter(request, response);
        }

        @Test
        @DisplayName("Receptionist should be denied POST to rooms")
        void receptionist_shouldBeDeniedPostToRooms() throws Exception {
            setupRequestPath("/rooms");
            User receptionist = new User(2, "Jane", "Smith", "reception", "reception@test.com", "hash", "RECEPTIONIST", null, null);
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute("authUser")).thenReturn(receptionist);
            when(request.getMethod()).thenReturn("POST");

            authFilter.doFilter(request, response, chain);

            verify(response).sendRedirect("/app/dashboard?error=Access%20Denied");
        }

        @Test
        @DisplayName("Receptionist should access rooms GET (view only)")
        void receptionist_shouldAccessRoomsGet() throws Exception {
            setupRequestPath("/rooms");
            User receptionist = new User(2, "Jane", "Smith", "reception", "reception@test.com", "hash", "RECEPTIONIST", null, null);
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute("authUser")).thenReturn(receptionist);
            when(request.getMethod()).thenReturn("GET");

            authFilter.doFilter(request, response, chain);

            verify(chain).doFilter(request, response);
        }

        @Test
        @DisplayName("Receptionist should access reservations")
        void receptionist_shouldAccessReservations() throws Exception {
            setupRequestPath("/reservations");
            User receptionist = new User(2, "Jane", "Smith", "reception", "reception@test.com", "hash", "RECEPTIONIST", null, null);
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute("authUser")).thenReturn(receptionist);
            when(request.getMethod()).thenReturn("GET");

            authFilter.doFilter(request, response, chain);

            verify(chain).doFilter(request, response);
        }

        @Test
        @DisplayName("Receptionist should access billing")
        void receptionist_shouldAccessBilling() throws Exception {
            setupRequestPath("/biling");
            User receptionist = new User(2, "Jane", "Smith", "reception", "reception@test.com", "hash", "RECEPTIONIST", null, null);
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute("authUser")).thenReturn(receptionist);
            when(request.getMethod()).thenReturn("GET");

            authFilter.doFilter(request, response, chain);

            verify(chain).doFilter(request, response);
        }
    }
}
