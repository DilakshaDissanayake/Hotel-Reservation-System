package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.config.ApplicationComponents;
import com.example.hotelreservationsystem.model.Rooms;
import com.example.hotelreservationsystem.service.ReservationService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/rooms/edit")
public class RoomUpdateController extends HttpServlet {

    private ReservationService reservationService;

    @Override
    public void init() {
        reservationService = ApplicationComponents.getInstance().getReservationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer roomId = parseRoomId(request.getParameter("id"));
        if (roomId == null) {
            response.sendRedirect(request.getContextPath() + "/rooms?error=Invalid%20room%20id");
            return;
        }

        Optional<Rooms> roomOptional = reservationService.getRoomById(roomId);
        if (roomOptional.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/rooms?error=Room%20not%20found");
            return;
        }

        request.setAttribute("message", request.getParameter("message"));
        request.setAttribute("error", request.getParameter("error"));
        request.setAttribute("room", roomOptional.get());
        request.setAttribute("title", "Edit Room");
        request.setAttribute("contentPage", "/WEB-INF/views/room/edit.jsp");

        request.getRequestDispatcher("/WEB-INF/views/include/app.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer roomId = parseRoomId(request.getParameter("id"));
        if (roomId == null) {
            response.sendRedirect(request.getContextPath() + "/rooms?error=Invalid%20room%20id");
            return;
        }

        String roomNumber = request.getParameter("roomNumber");
        String roomType = request.getParameter("roomType");
        String rate = request.getParameter("ratePerNight");
        String status = request.getParameter("status");
        String description = request.getParameter("description");

        try {
            double ratePerNight = Double.parseDouble(rate);
            reservationService.updateRoom(roomId, roomNumber, roomType, ratePerNight, status, description);
            response.sendRedirect(request.getContextPath() + "/rooms?message=Room%20updated%20successfully");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/rooms/edit?id=" + roomId + "&error=Invalid%20rate%20per%20night");
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage() == null ? "Unable%20to%20update%20room" : e.getMessage().replace(" ", "%20");
            response.sendRedirect(request.getContextPath() + "/rooms/edit?id=" + roomId + "&error=" + errorMessage);
        }
    }

    private Integer parseRoomId(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
