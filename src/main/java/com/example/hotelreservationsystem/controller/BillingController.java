package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.dto.BillDetailsDTO;
import com.example.hotelreservationsystem.dto.ReservationSummaryDTO;
import com.example.hotelreservationsystem.config.ApplicationComponents;
import com.example.hotelreservationsystem.service.ReservationService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/biling")
public class BillingController extends HttpServlet {

    private ReservationService reservationService;

    @Override
    public void init() {
        reservationService = ApplicationComponents.getInstance().getReservationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long reservationId = parseReservationId(request.getParameter("reservationId"));
        if (reservationId <= 0) {
            response.sendRedirect(request.getContextPath() + "/reservations");
            return;
        }

        Optional<ReservationSummaryDTO> reservation = reservationService.getReservationById(reservationId);
        if (reservation.isEmpty()) {
            request.setAttribute("error", "Reservation not found.");
            request.setAttribute("title", "Billing");
            request.setAttribute("contentPage", "/WEB-INF/views/biling/index.jsp");
            request.getRequestDispatcher("/WEB-INF/views/include/app.jsp").forward(request, response);
            return;
        }

        Optional<BillDetailsDTO> bill = reservationService.getBillByReservationId(reservationId);

        request.setAttribute("reservation", reservation.get());
        request.setAttribute("bill", bill.orElse(null));
        request.setAttribute("message", request.getParameter("message"));
        request.setAttribute("title", "Billing");
        request.setAttribute("contentPage", "/WEB-INF/views/biling/index.jsp");

        request.getRequestDispatcher("/WEB-INF/views/include/app.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long reservationId = parseReservationId(request.getParameter("reservationId"));
        if (reservationId <= 0) {
            response.sendRedirect(request.getContextPath() + "/reservations");
            return;
        }

        double extrasTotal = parseDoubleOrZero(request.getParameter("extrasTotal"));
        double discountAmount = parseDoubleOrZero(request.getParameter("discountAmount"));

        Optional<BillDetailsDTO> bill = reservationService.createBill(reservationId, extrasTotal, discountAmount);
        if (bill.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/biling?reservationId=" + reservationId + "&message=Bill generation failed");
            return;
        }

        boolean emailSent = reservationService.sendBillToGuest(reservationId);
        String message = emailSent ? "Payment saved. Bill emailed to guest." : "Payment saved. Bill ready to print.";
        response.sendRedirect(request.getContextPath() + "/biling?reservationId=" + reservationId + "&message=" + encode(message));
    }

    private long parseReservationId(String value) {
        if (value == null || value.trim().isEmpty()) {
            return -1;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private double parseDoubleOrZero(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        return Double.parseDouble(value.trim());
    }

    private String encode(String value) {
        return value.replace(" ", "%20");
    }
}
