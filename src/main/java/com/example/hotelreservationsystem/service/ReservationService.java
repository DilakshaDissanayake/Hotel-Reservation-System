package com.example.hotelreservationsystem.service;

import com.example.hotelreservationsystem.dto.BillDetailsDTO;
import com.example.hotelreservationsystem.dto.DashboardStatsDTO;
import com.example.hotelreservationsystem.dto.ReservationSummaryDTO;
import com.example.hotelreservationsystem.model.Rooms;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationService {

    DashboardStatsDTO getDashboardStats();

    List<Rooms> getAllRooms();

    int addRoom(String roomNumber,
                String roomType,
                double ratePerNight,
                String status,
                String description);

    Optional<Rooms> getRoomById(int roomId);

    boolean updateRoom(int roomId,
                       String roomNumber,
                       String roomType,
                       double ratePerNight,
                       String status,
                       String description);

    List<Rooms> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType);

    List<ReservationSummaryDTO> getReservations();

    Optional<ReservationSummaryDTO> getReservationById(long reservationId);

    long createReservation(int guestCount,
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
                           String phoneNumber);

    Optional<BillDetailsDTO> createBill(long reservationId, double extrasTotal, double discountAmount);

    Optional<BillDetailsDTO> getBillByReservationId(long reservationId);

    boolean sendBillToGuest(long reservationId);
}
