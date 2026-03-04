package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.config.ApplicationComponents;
import com.example.hotelreservationsystem.service.ReservationService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/rooms")
public class RoomController extends HttpServlet {

    private ReservationService reservationService;

    @Override
    public void init() {
        reservationService = ApplicationComponents.getInstance().getReservationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("message", request.getParameter("message"));
        request.setAttribute("error", request.getParameter("error"));
        request.setAttribute("rooms", reservationService.getAllRooms());
        request.setAttribute("title", "Rooms");
        request.setAttribute("contentPage", "/WEB-INF/views/room/index.jsp");

        request.getRequestDispatcher("/WEB-INF/views/include/app.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String roomNumber = request.getParameter("roomNumber");
        String roomType = request.getParameter("roomType");
        String rate = request.getParameter("ratePerNight");
        String status = request.getParameter("status");
        String description = request.getParameter("description");

        try {
            double ratePerNight = Double.parseDouble(rate);
            reservationService.addRoom(roomNumber, roomType, ratePerNight, status, description);
            response.sendRedirect(request.getContextPath() + "/rooms?message=Room%20added%20successfully");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/rooms?error=Invalid%20rate%20per%20night");
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage() == null ? "Unable to add room" : e.getMessage().replace(" ", "%20");
            response.sendRedirect(request.getContextPath() + "/rooms?error=" + errorMessage);
        }
    }
}
