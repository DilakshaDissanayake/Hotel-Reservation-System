package com.example.hotelreservationsystem.model;

public class Bills {
    private int billId;
    private int reservation_id;
    private int night;
    private double ratePerNight ;
    private double extrasTotal;
    private double discountAmount;
    private double subTotal;
    private double total;
    private String generatedAt;

    public Bills() {}

    public Bills(int billId, int reservation_id, int night, double ratePerNight, double extrasTotal, double discountAmount, double subTotal, double total, String generatedAt) {
        this.billId = billId;
        this.reservation_id = reservation_id;
        this.night = night;
        this.ratePerNight = ratePerNight;
        this.extrasTotal = extrasTotal;
        this.discountAmount = discountAmount;
        this.subTotal = subTotal;
        this.total = total;
        this.generatedAt = generatedAt;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getReservation_id() {
        return reservation_id;
    }

    public void setReservation_id(int reservation_id) {
        this.reservation_id = reservation_id;
    }

    public int getNight() {
        return night;
    }

    public void setNight(int night) {
        this.night = night;
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

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }
}
