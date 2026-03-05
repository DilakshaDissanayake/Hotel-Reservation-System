package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.config.ApplicationComponents;
import com.example.hotelreservationsystem.service.ReservationService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.*;

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
        List<com.example.hotelreservationsystem.model.Rooms> rooms = reservationService.getAllRooms();
        Map<Integer, List<com.example.hotelreservationsystem.dto.ReservationSummaryDTO>> roomBookingMap = new HashMap<>();
        
        if (rooms != null) {
            for (com.example.hotelreservationsystem.model.Rooms room : rooms) {
                roomBookingMap.put(room.getId(), reservationService.getRoomBookingDates(room.getId()));
            }
        }

        request.setAttribute("rooms", rooms);
        request.setAttribute("roomBookings", roomBookingMap);
        request.setAttribute("title", "Rooms");
        request.setAttribute("contentPage", "/WEB-INF/views/room/index.jsp");

        request.getRequestDispatcher("/WEB-INF/views/include/app.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        com.example.hotelreservationsystem.model.User authUser = (session != null) ? (com.example.hotelreservationsystem.model.User) session.getAttribute("authUser") : null;
        if (authUser == null || !"ADMIN".equals(authUser.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

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
