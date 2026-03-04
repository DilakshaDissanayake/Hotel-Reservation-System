package com.example.hotelreservationsystem.config;

import com.example.hotelreservationsystem.dao.ReservationDAO;
import com.example.hotelreservationsystem.dao.UserDAO;
import com.example.hotelreservationsystem.dao.impl.ReservationDAOImpl;
import com.example.hotelreservationsystem.dao.impl.UserDAOImpl;
import com.example.hotelreservationsystem.service.EmailService;
import com.example.hotelreservationsystem.service.ReservationService;
import com.example.hotelreservationsystem.service.UserService;
import com.example.hotelreservationsystem.service.impl.EmailServiceImpl;
import com.example.hotelreservationsystem.service.impl.ReservationServiceImpl;
import com.example.hotelreservationsystem.service.impl.UserServiceImpl;

public final class ApplicationComponents {

    private static final ApplicationComponents INSTANCE = new ApplicationComponents();

    private final UserService userService;
    private final EmailService emailService;
    private final ReservationService reservationService;

    private ApplicationComponents() {
        UserDAO userDAO = new UserDAOImpl();
        ReservationDAO reservationDAO = new ReservationDAOImpl();
        this.userService = new UserServiceImpl(userDAO);
        this.emailService = new EmailServiceImpl();
        this.reservationService = new ReservationServiceImpl(reservationDAO, emailService);
    }

    public static ApplicationComponents getInstance() {
        return INSTANCE;
    }

    public UserService getUserService() {
        return userService;
    }

    public EmailService getEmailService() {
        return emailService;
    }

    public ReservationService getReservationService() {
        return reservationService;
    }
}