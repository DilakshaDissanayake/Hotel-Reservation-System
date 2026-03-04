package com.example.hotelreservationsystem.dao;

import com.example.hotelreservationsystem.dto.BillDetailsDTO;
import com.example.hotelreservationsystem.dto.DashboardStatsDTO;
import com.example.hotelreservationsystem.dto.ReservationSummaryDTO;
import com.example.hotelreservationsystem.model.Rooms;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationDAO {

    DashboardStatsDTO getDashboardStats();

    List<Rooms> findAllRooms();

    int createRoom(String roomNumber,
                   String roomType,
                   double ratePerNight,
                   String status,
                   String description);

    int createRoomWithFacilities(String roomNumber,
                                 String roomType,
                                 double ratePerNight,
                                 String status,
                                 String description,
                                 List<com.example.hotelreservationsystem.model.RoomFacilities> facilities);

    boolean updateRoomWithFacilities(int roomId,
                                     String roomNumber,
                                     String roomType,
                                     double ratePerNight,
                                     String status,
                                     String description,
                                     List<com.example.hotelreservationsystem.model.RoomFacilities> facilities);

    Optional<Rooms> getRoomById(int roomId);

    List<com.example.hotelreservationsystem.model.Facilities> getAllFacilities();

    List<com.example.hotelreservationsystem.model.RoomFacilities> getRoomFacilities(int roomId);

    List<Rooms> findAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType);

    boolean isRoomAvailable(int roomId, LocalDate checkInDate, LocalDate checkOutDate);

    long createReservationWithPrimaryGuest(int guestCount,
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

    List<ReservationSummaryDTO> findAllReservationSummaries();

    Optional<ReservationSummaryDTO> findReservationSummaryById(long reservationId);

    long createOrUpdateBill(long reservationId,
                            int nights,
                            double ratePerNight,
                            double extrasTotal,
                            double discountAmount,
                            double subTotal,
                            double total);

    Optional<BillDetailsDTO> findBillDetailsByReservationId(long reservationId);
}