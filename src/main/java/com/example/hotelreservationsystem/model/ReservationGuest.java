package com.example.hotelreservationsystem.model;

public class ReservationGuest {
    private int id;
    private int reservationId;
    private String fullName;
    private String age;
    private String gender;
    private String nic;
    private String passportNumber;
    private int isPrimary;
    private String email;
    private String phoneNumber;
    private String createdAt;

    public ReservationGuest() {}

    public ReservationGuest(int id, int reservationId, String fullName, String age, String gender, String nic, String passportNumber, int isPrimary, String email, String phoneNumber, String createdAt) {
        this.id = id;
        this.reservationId = reservationId;
        this.fullName = fullName;
        this.age = age;
        this.gender = gender;
        this.nic = nic;
        this.passportNumber = passportNumber;
        this.isPrimary = isPrimary;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public int getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(int isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
