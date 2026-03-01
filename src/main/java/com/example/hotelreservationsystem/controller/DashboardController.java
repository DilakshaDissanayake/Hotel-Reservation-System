package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardController extends HttpServlet {

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

        send(request, response);
    }

    private void send(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("title", "Dashboard");
        request.setAttribute("contentPage", "/WEB-INF/views/dashboard/index.jsp");

        request.getRequestDispatcher("/WEB-INF/views/include/app.jsp")
                .forward(request, response);
    }
}
