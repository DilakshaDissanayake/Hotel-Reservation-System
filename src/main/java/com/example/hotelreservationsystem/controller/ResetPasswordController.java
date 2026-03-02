package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.config.ApplicationComponents;
import com.example.hotelreservationsystem.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@WebServlet("/reset-password")
public class ResetPasswordController extends HttpServlet {

    private UserService userService;

    @Override
    public void init() {
        userService = ApplicationComponents.getInstance().getUserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        ensureCsrfToken(session);

        String token = request.getParameter("token");
        boolean tokenValid = userService.isResetTokenValid(token);

        if (!tokenValid) {
            request.setAttribute("error", "Invalid or expired reset link.");
        } else {
            request.setAttribute("resetToken", token);
        }

        request.setAttribute("tokenValid", tokenValid);
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

        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (token == null || token.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            request.setAttribute("error", "Invalid request. Please try again.");
            request.setAttribute("tokenValid", false);
            forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Password confirmation does not match.");
            request.setAttribute("tokenValid", true);
            request.setAttribute("resetToken", token);
            forward(request, response);
            return;
        }

        if (newPassword.trim().length() < 8) {
            request.setAttribute("error", "Password must be at least 8 characters long.");
            request.setAttribute("tokenValid", true);
            request.setAttribute("resetToken", token);
            forward(request, response);
            return;
        }

        boolean isResetSuccessful = userService.resetPassword(token.trim(), newPassword);

        if (isResetSuccessful) {
            String status = URLEncoder.encode("reset-success", StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/login?status=" + status);
        } else {
            request.setAttribute("error", "Invalid or expired token.");
            request.setAttribute("tokenValid", false);
            forward(request, response);
        }
    }

    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("title", "Reset Password");
        request.setAttribute("contentPage", "/WEB-INF/views/auth/reset-password.jsp");
        request.getRequestDispatcher("/WEB-INF/views/include/auth-layout.jsp").forward(request, response);
    }

    private void ensureCsrfToken(HttpSession session) {
        if (session.getAttribute("csrfToken") == null) {
            session.setAttribute("csrfToken", UUID.randomUUID().toString());
        }
    }

    private boolean isValidCsrf(HttpSession session, String requestToken) {
        if (session == null || requestToken == null) {
            return false;
        }
        String sessionToken = (String) session.getAttribute("csrfToken");
        return sessionToken != null && sessionToken.equals(requestToken);
    }
}