package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.config.ApplicationComponents;
import com.example.hotelreservationsystem.service.EmailService;
import com.example.hotelreservationsystem.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/forgot-password")
public class ForgotPasswordController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ForgotPasswordController.class.getName());

    private UserService userService;
    private EmailService emailService;

    @Override
    public void init() {
        ApplicationComponents components = ApplicationComponents.getInstance();
        userService = components.getUserService();
        emailService = components.getEmailService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ensureCsrfToken(session);

        forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (!isValidCsrf(session, request.getParameter("csrfToken"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF Token");
            return;
        }

        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Email is required.");
            forward(request, response);
            return;
        }

        Optional<String> tokenOptional = userService.createPasswordResetToken(email.trim());
        boolean emailSendFailed = false;
        if (tokenOptional.isPresent()) {
            String resetLink = buildResetLink(request, tokenOptional.get());
            try {
                emailService.sendPasswordResetEmail(email.trim(), resetLink);
            } catch (RuntimeException ex) {
                LOGGER.log(Level.WARNING, "Password reset email send failed", ex);
                request.setAttribute("error", "Unable to send reset email right now. Please try again later.");
                emailSendFailed = true;
            }
        }

        if (!emailSendFailed) {
            request.setAttribute("message", "If an account exists for this email, a reset link has been sent.");
        }
        forward(request, response);
    }

    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("title", "Forgot Password");
        request.setAttribute("contentPage", "/WEB-INF/views/auth/forgot-password.jsp");
        request.getRequestDispatcher("/WEB-INF/views/include/auth-layout.jsp").forward(request, response);
    }

    private void ensureCsrfToken(HttpSession session) {
        if (session.getAttribute("csrfToken") == null) {
            session.setAttribute("csrfToken", java.util.UUID.randomUUID().toString());
        }
    }

    private boolean isValidCsrf(HttpSession session, String requestToken) {
        if (session == null || requestToken == null) {
            return false;
        }
        String sessionToken = (String) session.getAttribute("csrfToken");
        return sessionToken != null && sessionToken.equals(requestToken);
    }

    private String buildResetLink(HttpServletRequest request, String token) {
        StringBuilder url = new StringBuilder();
        url.append(request.getScheme()).append("://").append(request.getServerName());
        if (("http".equalsIgnoreCase(request.getScheme()) && request.getServerPort() != 80)
                || ("https".equalsIgnoreCase(request.getScheme()) && request.getServerPort() != 443)) {
            url.append(":").append(request.getServerPort());
        }
        url.append(request.getContextPath()).append("/reset-password?token=").append(token);
        return url.toString();
    }
}