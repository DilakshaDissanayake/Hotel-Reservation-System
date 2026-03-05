package com.example.hotelreservationsystem.service.impl;

import com.example.hotelreservationsystem.dao.ReservationDAO;
import com.example.hotelreservationsystem.dto.BillDetailsDTO;
import com.example.hotelreservationsystem.dto.DashboardStatsDTO;
import com.example.hotelreservationsystem.dto.ReservationSummaryDTO;
import com.example.hotelreservationsystem.model.Rooms;
import com.example.hotelreservationsystem.service.EmailService;
import com.example.hotelreservationsystem.service.ReservationService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class ReservationServiceImpl implements ReservationService {

    private final ReservationDAO reservationDAO;
    private final EmailService emailService;

    public ReservationServiceImpl(ReservationDAO reservationDAO, EmailService emailService) {
        this.reservationDAO = reservationDAO;
        this.emailService = emailService;
    }

    @Override
    public DashboardStatsDTO getDashboardStats() {
        return reservationDAO.getDashboardStats();
    }

    @Override
    public List<Rooms> getAllRooms() {
        return reservationDAO.findAllRooms();
    }

    @Override
    public int addRoom(String roomNumber,
                       String roomType,
                       double ratePerNight,
                       String status,
                       String description) {
        validateRoomDetails(roomNumber, roomType, ratePerNight, status);

        return reservationDAO.createRoom(
                roomNumber.trim().toUpperCase(),
                roomType.trim().toUpperCase(),
                ratePerNight,
                status.trim().toUpperCase(),
                isBlank(description) ? null : description.trim()
        );
    }

    private void validateRoomDetails(String roomNumber, String roomType, double ratePerNight, String status) {
        if (isBlank(roomNumber)) {
            throw new IllegalArgumentException("Room number is required.");
        }
        if (!roomNumber.trim().matches("^[A-Za-z0-9\\-]+$")) {
            throw new IllegalArgumentException("Room number can only contain alphanumeric characters and hyphens.");
        }
        if (isBlank(roomType)) {
            throw new IllegalArgumentException("Room type is required.");
        }
        List<String> validTypes = List.of("SINGLE", "DOUBLE", "DELUXE", "SUITE");
        if (!validTypes.contains(roomType.trim().toUpperCase())) {
            throw new IllegalArgumentException("Invalid room type.");
        }
        if (ratePerNight <= 0) {
            throw new IllegalArgumentException("Rate per night must be greater than zero.");
        }
        if (isBlank(status)) {
            throw new IllegalArgumentException("Room status is required.");
        }
        List<String> validStatus = List.of("AVAILABLE", "MAINTENANCE");
        if (!validStatus.contains(status.trim().toUpperCase())) {
            throw new IllegalArgumentException("Invalid room status.");
        }
    }

    @Override
    public List<Rooms> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        return reservationDAO.findAvailableRooms(checkInDate, checkOutDate, roomType);
    }

    @Override
    public List<ReservationSummaryDTO> getReservations() {
        return reservationDAO.findAllReservationSummaries();
    }

    @Override
    public Optional<ReservationSummaryDTO> getReservationById(long reservationId) {
        return reservationDAO.findReservationSummaryById(reservationId);
    }

    @Override
    public long createReservation(int guestCount,
                                  String address,
                                  String contactNumber,
                                  String roomType,
                                  LocalDate checkInDate,
                                  LocalDate checkOutDate,
                                  int roomId,
                                  String guestFullName,
                                  Integer guestAge,
                                  String nic,
                                  String passportNo,
                                  String email,
                                  String phoneNumber) {

        validateReservation(guestCount, contactNumber, roomType, checkInDate, checkOutDate, roomId, guestFullName, email);

        if (!reservationDAO.isRoomAvailable(roomId, checkInDate, checkOutDate)) {
            throw new IllegalArgumentException("Selected room is not available for the date range.");
        }

        return reservationDAO.createReservationWithPrimaryGuest(
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
    }

    @Override
    public Optional<BillDetailsDTO> createBill(long reservationId, double extrasTotal, double discountAmount) {
        Optional<ReservationSummaryDTO> reservationOptional = reservationDAO.findReservationSummaryById(reservationId);
        if (reservationOptional.isEmpty()) {
            return Optional.empty();
        }

        ReservationSummaryDTO reservation = reservationOptional.get();

        LocalDate checkInDate = LocalDate.parse(reservation.getCheckInDate());
        LocalDate checkOutDate = LocalDate.parse(reservation.getCheckOutDate());

        int nights = (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (nights <= 0) {
            nights = 1;
        }

        double roomTotal = reservation.getRatePerNight() * nights;
        double safeExtras = Math.max(0, extrasTotal);
        double safeDiscount = Math.max(0, discountAmount);
        double subTotal = roomTotal + safeExtras;
        double total = Math.max(0, subTotal - safeDiscount);

        reservationDAO.createOrUpdateBill(
                reservationId,
                nights,
                reservation.getRatePerNight(),
                safeExtras,
                safeDiscount,
                subTotal,
                total
        );

        return reservationDAO.findBillDetailsByReservationId(reservationId);
    }

    @Override
    public Optional<BillDetailsDTO> getBillByReservationId(long reservationId) {
        return reservationDAO.findBillDetailsByReservationId(reservationId);
    }

    @Override
    public boolean sendBillToGuest(long reservationId) {
        Optional<BillDetailsDTO> billOptional = reservationDAO.findBillDetailsByReservationId(reservationId);
        if (billOptional.isEmpty()) {
            return false;
        }

        BillDetailsDTO bill = billOptional.get();
        if (isBlank(bill.getGuestEmail())) {
            return false;
        }

        emailService.sendBillEmail(bill.getGuestEmail(), buildBillMessage(bill));
        return true;
    }

    @Override
    public Optional<Rooms> getRoomById(int roomId) {
        if (roomId <= 0) {
            return Optional.empty();
        }
        return reservationDAO.getRoomById(roomId);
    }

    @Override
    public boolean updateRoom(int roomId,
                              String roomNumber,
                              String roomType,
                              double ratePerNight,
                              String status,
                              String description) {
        if (roomId <= 0) {
            throw new IllegalArgumentException("Room id is required.");
        }
        validateRoomDetails(roomNumber, roomType, ratePerNight, status);

        boolean updated = reservationDAO.updateRoomWithFacilities(
                roomId,
                roomNumber.trim().toUpperCase(),
                roomType.trim().toUpperCase(),
                ratePerNight,
                status.trim().toUpperCase(),
                isBlank(description) ? null : description.trim(),
                null
        );
        if (!updated) {
            throw new IllegalArgumentException("Room not found.");
        }
        return true;
    }

    private void validateReservation(int guestCount,
                                     String contactNumber,
                                     String roomType,
                                     LocalDate checkInDate,
                                     LocalDate checkOutDate,
                                     int roomId,
                                     String guestFullName,
                                     String email) {
        if (guestCount <= 0) {
            throw new IllegalArgumentException("Guest count must be at least 1.");
        }
        if (roomId <= 0) {
            throw new IllegalArgumentException("Please select a valid room.");
        }
        if (isBlank(contactNumber)) {
            throw new IllegalArgumentException("Contact number is required.");
        }
        if (isBlank(roomType)) {
            throw new IllegalArgumentException("Room type is required.");
        }
        if (checkInDate == null || checkOutDate == null || !checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }
        if (isBlank(guestFullName)) {
            throw new IllegalArgumentException("Primary guest full name is required.");
        }
        if (isBlank(email)) {
            throw new IllegalArgumentException("Primary guest email is required.");
        }
    }

    private String buildBillMessage(BillDetailsDTO bill) {
        return "Dear " + safe(bill.getGuestName()) + ",\n\n"
                + "Thank you for choosing Ocean View Resort. Your payment was completed successfully.\n\n"
                + "Invoice Details\n"
                + "Bill ID: " + bill.getBillId() + "\n"
                + "Reservation ID: " + bill.getReservationId() + "\n"
                + "Room: " + safe(bill.getRoomNumber()) + " (" + safe(bill.getRoomType()) + ")\n"
                + "Stay: " + safe(bill.getCheckInDate()) + " to " + safe(bill.getCheckOutDate()) + "\n"
                + "Nights: " + bill.getNights() + "\n"
                + "Rate per Night: " + bill.getRatePerNight() + "\n"
                + "Extras: " + bill.getExtrasTotal() + "\n"
                + "Discount: " + bill.getDiscountAmount() + "\n"
                + "Sub Total: " + bill.getSubTotal() + "\n"
                + "Total Paid: " + bill.getTotal() + "\n"
                + "Generated At: " + safe(bill.getGeneratedAt()) + "\n\n"
                + "Best regards,\n"
                + "Ocean View Resort";
    }

    private String safe(String value) {
        return value == null ? "-" : value;
    }

    @Override
    public List<ReservationSummaryDTO> getRoomBookingDates(int roomId) {
        if (roomId <= 0) {
            return java.util.Collections.emptyList();
        }
        return reservationDAO.findUpcomingReservationsByRoom(roomId);
    }

    @Override
    public boolean completeReservation(long reservationId) {
        Optional<ReservationSummaryDTO> reservationOptional = reservationDAO.findReservationSummaryById(reservationId);
        if (reservationOptional.isEmpty()) {
            return false;
        }

        ReservationSummaryDTO reservation = reservationOptional.get();

        boolean statusUpdated = reservationDAO.updateReservationStatus(reservationId, "COMPLETED");

        boolean roomUpdated = reservationDAO.updateRoomStatus(reservation.getRoomId(), "AVAILABLE");

        return statusUpdated && roomUpdated;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
