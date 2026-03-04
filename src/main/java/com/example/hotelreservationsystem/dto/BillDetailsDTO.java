package com.example.hotelreservationsystem.dto;

public class BillDetailsDTO {
    private long billId;
    private long reservationId;
    private String guestName;
    private String guestEmail;
    private String roomNumber;
    private String roomType;
    private String checkInDate;
    private String checkOutDate;
    private int nights;
    private double ratePerNight;
    private double extrasTotal;
    private double discountAmount;
    private double subTotal;
    private double total;
    private String generatedAt;

    public BillDetailsDTO() {
    }

    public BillDetailsDTO(long billId, long reservationId, String guestName, String guestEmail, String roomNumber,
                          String roomType, String checkInDate, String checkOutDate, int nights, double ratePerNight,
                          double extrasTotal, double discountAmount, double subTotal, double total, String generatedAt) {
        this.billId = billId;
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.nights = nights;
        this.ratePerNight = ratePerNight;
        this.extrasTotal = extrasTotal;
        this.discountAmount = discountAmount;
        this.subTotal = subTotal;
        this.total = total;
        this.generatedAt = generatedAt;
    }

    public long getBillId() {
        return billId;
    }

    public void setBillId(long billId) {
        this.billId = billId;
    }

    public long getReservationId() {
        return reservationId;
    }

    public void setReservationId(long reservationId) {
        this.reservationId = reservationId;
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

    public int getNights() {
        return nights;
    }

    public void setNights(int nights) {
        this.nights = nights;
    }

    public double getRatePerNight() {
        return ratePerNight;
    }

    public void setRatePerNight(double ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    public double getExtrasTotal() {
        return extrasTotal;
    }

    public void setExtrasTotal(double extrasTotal) {
        this.extrasTotal = extrasTotal;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    /**
     * Convenience getter: room charge (ratePerNight × nights).
     * Use ${bill.roomCharges} in JSP instead of EL arithmetic which
     * does not work reliably for double multiplication.
     */
    public double getRoomCharges() {
        return ratePerNight * nights;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }
}