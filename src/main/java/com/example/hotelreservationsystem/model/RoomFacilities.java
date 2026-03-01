package com.example.hotelreservationsystem.model;

public class RoomFacilities {
    private int  roomId;
    private int facilityId;
    private double extraPricePerNight;

    public RoomFacilities() {}

    public RoomFacilities(int roomId, int facilityId, double extraPricePerNight) {
        this.roomId = roomId;
        this.facilityId = facilityId;
        this.extraPricePerNight = extraPricePerNight;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    public double getExtraPricePerNight() {
        return extraPricePerNight;
    }

    public void setExtraPricePerNight(double extraPricePerNight) {
        this.extraPricePerNight = extraPricePerNight;
    }
}
