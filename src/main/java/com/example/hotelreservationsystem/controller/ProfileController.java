package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/profile")
public class ProfileController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("authUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User authUser = (User) session.getAttribute("authUser");
        request.setAttribute("authUser", authUser);

        request.setAttribute("title", "My Profile");
        request.setAttribute("contentPage", "/WEB-INF/views/profile/index.jsp");

        request.getRequestDispatcher("/WEB-INF/views/include/app.jsp")
                .forward(request, response);
    }
}
