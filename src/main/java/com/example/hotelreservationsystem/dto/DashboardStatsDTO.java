package com.example.hotelreservationsystem.dto;

public class DashboardStatsDTO {
    private int totalRooms;
    private int availableRooms;
    private int totalReservations;
    private int totalBills;
    private double totalRevenue;

    public DashboardStatsDTO() {
    }

    public DashboardStatsDTO(int totalRooms, int availableRooms, int totalReservations, int totalBills, double totalRevenue) {
        this.totalRooms = totalRooms;
        this.availableRooms = availableRooms;
        this.totalReservations = totalReservations;
        this.totalBills = totalBills;
        this.totalRevenue = totalRevenue;
    }

    public int getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(int totalRooms) {
        this.totalRooms = totalRooms;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(int availableRooms) {
        this.availableRooms = availableRooms;
    }

    public int getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(int totalReservations) {
        this.totalReservations = totalReservations;
    }

    public int getTotalBills() {
        return totalBills;
    }

    public void setTotalBills(int totalBills) {
        this.totalBills = totalBills;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}