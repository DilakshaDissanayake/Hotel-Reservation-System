package com.example.hotelreservationsystem.dto;

public class ReservationSummaryDTO {
    private long reservationId;
    private int guestCount;
    private String contactNumber;
    private String checkInDate;
    private String checkOutDate;
    private int roomId;
    private String roomNumber;
    private String roomType;
    private double ratePerNight;
    private String guestName;
    private String guestEmail;

    public ReservationSummaryDTO() {
    }

    public ReservationSummaryDTO(long reservationId, int guestCount, String contactNumber, String checkInDate,
                                 String checkOutDate, int roomId, String roomNumber, String roomType,
                                 double ratePerNight, String guestName, String guestEmail) {
        this.reservationId = reservationId;
        this.guestCount = guestCount;
        this.contactNumber = contactNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.ratePerNight = ratePerNight;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
    }

    public long getReservationId() {
        return reservationId;
    }

    public void setReservationId(long reservationId) {
        this.reservationId = reservationId;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getRatePerNight() {
        return ratePerNight;
    }

    public void setRatePerNight(double ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }
}