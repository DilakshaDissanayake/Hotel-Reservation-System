package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.config.ApplicationComponents;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/staff")
public class StaffController extends HttpServlet {

    private UserService userService;

    @Override
    public void init() {
        userService = ApplicationComponents.getInstance().getUserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User authUser = (session != null) ? (User) session.getAttribute("authUser") : null;

        if (authUser == null || !"ADMIN".equals(authUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/dashboard?error=Access%20Denied");
            return;
        }

        request.setAttribute("users", userService.getAllUsers());
        request.setAttribute("title", "Staff Management");
        request.setAttribute("contentPage", "/WEB-INF/views/staff/index.jsp");

        request.getRequestDispatcher("/WEB-INF/views/include/app.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User authUser = (session != null) ? (User) session.getAttribute("authUser") : null;

        if (authUser == null || !"ADMIN".equals(authUser.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        try {
            userService.createUser(firstName, lastName, username, email, password, role);
            response.sendRedirect(request.getContextPath() + "/staff?message=Staff%20member%20added%20successfully");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("firstNameAttr", firstName);
            request.setAttribute("lastNameAttr", lastName);
            request.setAttribute("usernameAttr", username);
            request.setAttribute("emailAttr", email);
            request.setAttribute("roleAttr", role);
            doGet(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "An unexpected error occurred: " + e.getMessage());
            doGet(request, response);
        }
    }
}
