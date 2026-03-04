package com.example.hotelreservationsystem.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);

    void sendPasswordResetEmail(String to, String resetLink);

    void sendBillEmail(String to, String billBody);
}