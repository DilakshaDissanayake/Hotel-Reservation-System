package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.config.ApplicationComponents;
import com.example.hotelreservationsystem.dto.DashboardStatsDTO;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.service.ReservationService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardController extends HttpServlet {

    private ReservationService reservationService;

    @Override
    public void init() {
        reservationService = ApplicationComponents.getInstance().getReservationService();
    }

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
        DashboardStatsDTO stats = reservationService.getDashboardStats();
        request.setAttribute("stats", stats);

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
