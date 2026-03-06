package com.example.hotelreservationsystem.integration;

import com.example.hotelreservationsystem.controller.LogoutController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Integration tests for Controller layer.
 * TC10 – Safe Exit: Tests session invalidation flows.
 * Uses service() which is public in HttpServlet to invoke GET/POST.
 */
@ExtendWith(MockitoExtension.class)
class ControllerIntegrationTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;


    @Nested
    @DisplayName("TC10 – Logout / Safe Exit Tests")
    class LogoutTests {

        @Test
        @DisplayName("Logout GET should invalidate session and redirect to login")
        void logout_shouldInvalidateSession_andRedirect() throws Exception {
            LogoutController controller = new LogoutController();
            when(request.getMethod()).thenReturn("GET");
            when(request.getSession(false)).thenReturn(session);
            when(request.getContextPath()).thenReturn("/app");

            controller.service(request, response);

            verify(session).invalidate();
            verify(response).sendRedirect("/app/login");
        }

        @Test
        @DisplayName("Logout with no session should still redirect")
        void logout_noSession_shouldRedirect() throws Exception {
            LogoutController controller = new LogoutController();
            when(request.getMethod()).thenReturn("GET");
            when(request.getSession(false)).thenReturn(null);
            when(request.getContextPath()).thenReturn("/app");

            controller.service(request, response);

            verify(response).sendRedirect("/app/login");
        }

        @Test
        @DisplayName("Logout POST should behave same as GET")
        void logoutPost_shouldBehaveSameAsGet() throws Exception {
            LogoutController controller = new LogoutController();
            when(request.getMethod()).thenReturn("POST");
            when(request.getSession(false)).thenReturn(session);
            when(request.getContextPath()).thenReturn("/app");

            controller.service(request, response);

            verify(session).invalidate();
            verify(response).sendRedirect("/app/login");
        }
    }
}
