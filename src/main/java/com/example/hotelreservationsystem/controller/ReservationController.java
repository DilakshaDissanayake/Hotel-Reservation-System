package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.config.ApplicationComponents;
import com.example.hotelreservationsystem.dto.ReservationSummaryDTO;
import com.example.hotelreservationsystem.service.ReservationService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/reservations")
public class ReservationController extends HttpServlet {

    private ReservationService reservationService;

    @Override
    public void init() {
        reservationService = ApplicationComponents.getInstance().getReservationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if ("new".equalsIgnoreCase(action)) {
            showCreatePage(request, response);
            return;
        }

        String statusFilter = request.getParameter("status");
        List<ReservationSummaryDTO> reservations = reservationService.getReservations();
        
        if ("history".equalsIgnoreCase(statusFilter)) {
            reservations = reservations.stream()
                    .filter(r -> "COMPLETED".equals(r.getStatus()) || "CANCELLED".equals(r.getStatus()))
                    .collect(java.util.stream.Collectors.toList());
            request.setAttribute("title", "Reservation History");
        } else {
            reservations = reservations.stream()
                    .filter(r -> "CONFIRMED".equals(r.getStatus()) || r.getStatus() == null)
                    .collect(java.util.stream.Collectors.toList());
            request.setAttribute("title", "Active Reservations");
        }

        request.setAttribute("reservations", reservations);
        request.setAttribute("statusFilter", statusFilter);
        request.setAttribute("contentPage", "/WEB-INF/views/reservations/index.jsp");

        request.getRequestDispatcher("/WEB-INF/views/include/app.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int guestCount = parseIntOrDefault(request.getParameter("guestCount"));
            String address = request.getParameter("address");
            String contactNumber = request.getParameter("contactNumber");
            String roomType = request.getParameter("roomType");
            LocalDate checkInDate = LocalDate.parse(request.getParameter("checkInDate"));
            LocalDate checkOutDate = LocalDate.parse(request.getParameter("checkOutDate"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String guestFullName = request.getParameter("guestFullName");
            Integer guestAge = parseNullableInt(request.getParameter("guestAge"));
            String nic = request.getParameter("nic");
            String passportNo = request.getParameter("passportNo");
            String email = request.getParameter("email");
            String phoneNumber = request.getParameter("phoneNumber");

            long reservationId = reservationService.createReservation(
                    guestCount,
                    address,
                    contactNumber,
                    roomType,
                    checkInDate,
                    checkOutDate,
                    roomId,
                    guestFullName,
                    guestAge,
                    nic,
                    passportNo,
                    email,
                    phoneNumber
            );

            response.sendRedirect(request.getContextPath() + "/biling?reservationId=" + reservationId);
        } catch (IllegalArgumentException | java.time.format.DateTimeParseException ex) {
            String errorMsg = ex instanceof java.time.format.DateTimeParseException ? "Invalid date format." : ex.getMessage();
            request.setAttribute("error", errorMsg);
            
            // Re-set parameters as attributes to preserve form data
            request.setAttribute("guestCountAttr", request.getParameter("guestCount"));
            request.setAttribute("addressAttr", request.getParameter("address"));
            request.setAttribute("contactNumberAttr", request.getParameter("contactNumber"));
            request.setAttribute("roomTypeAttr", request.getParameter("roomType"));
            request.setAttribute("checkInDateAttr", request.getParameter("checkInDate"));
            request.setAttribute("checkOutDateAttr", request.getParameter("checkOutDate"));
            request.setAttribute("roomIdAttr", request.getParameter("roomId"));
            request.setAttribute("guestFullNameAttr", request.getParameter("guestFullName"));
            request.setAttribute("guestAgeAttr", request.getParameter("guestAge"));
            request.setAttribute("nicAttr", request.getParameter("nic"));
            request.setAttribute("passportNoAttr", request.getParameter("passportNo"));
            request.setAttribute("emailAttr", request.getParameter("email"));
            request.setAttribute("phoneNumberAttr", request.getParameter("phoneNumber"));

            showCreatePage(request, response);
        } catch (Exception ex) {
            request.setAttribute("error", "An unexpected error occurred: " + ex.getMessage());
            showCreatePage(request, response);
        }
    }

    private void showCreatePage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("rooms", reservationService.getAllRooms());
        request.setAttribute("title", "Create Reservation");
        request.setAttribute("contentPage", "/WEB-INF/views/reservations/create.jsp");

        request.getRequestDispatcher("/WEB-INF/views/include/app.jsp")
                .forward(request, response);
    }

    private int parseIntOrDefault(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 1;
        }
        return Integer.parseInt(value.trim());
    }

    private Integer parseNullableInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Integer.parseInt(value.trim());
    }
}
