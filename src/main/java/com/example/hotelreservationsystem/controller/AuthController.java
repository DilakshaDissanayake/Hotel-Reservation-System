package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.service.UserService;
import com.example.hotelreservationsystem.service.impl.UserServiceImpl;
import com.example.hotelreservationsystem.dao.impl.UserDAOImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;


@WebServlet("/login")
public class AuthController extends HttpServlet {

    private UserService userService;

    @Override
    public void init() {
        userService = new UserServiceImpl(new UserDAOImpl());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);

        if (session.getAttribute("csrfToken") == null) {
            session.setAttribute("csrfToken", generateCsrfToken());
        }

        forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String sessionToken = (String) session.getAttribute("csrfToken");
        String requestToken = request.getParameter("csrfToken");

        if (sessionToken == null || !sessionToken.equals(requestToken)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF Token");
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (isBlank(username) || isBlank(password)) {
            request.setAttribute("error", "Username and password are required.");
            forward(request, response);
            return;
        }

        Optional<User> authUser = userService.authenticate(username.trim(), password.trim());
        System.out.println("auth User" + authUser);

        if (authUser.isPresent()) {

            session.invalidate();

            HttpSession newSession = request.getSession(true);

            newSession.setAttribute("authUser", authUser.get());
            newSession.setMaxInactiveInterval(30 * 60);

            newSession.setAttribute("csrfToken", generateCsrfToken());

            response.sendRedirect(request.getContextPath() + "/");

        } else {
            request.setAttribute("error", "Invalid username or password.");
            forward(request, response);
        }
    }


    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("title", "Login");
        request.setAttribute("contentPage", "/WEB-INF/views/auth/login.jsp");

        request.getRequestDispatcher("/WEB-INF/views/include/auth-layout.jsp")
                .forward(request, response);
    }

    private String generateCsrfToken() {
        return UUID.randomUUID().toString();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}