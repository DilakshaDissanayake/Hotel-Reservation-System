package com.example.hotelreservationsystem.service.impl;

import com.example.hotelreservationsystem.dao.UserDAO;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.service.UserService;
import com.example.hotelreservationsystem.util.PasswordUtil;

import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public Optional<User> authenticate(String username, String password) {

        System.out.println("Auth Pass Imp: username='" + username + "', password='" + password + "'");

        System.out.println("Auth Pass Imp " +username +password);
        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {
            return Optional.empty();
        }
        return userDAO.findByUsername(username.trim())
                .map(user -> {
                    System.out.println("Stored password hash for user '" + username + "': " + user.getPasswordHash());
                    System.out.println("User Data '" + user.getUsername() + "': " + user.getRole());
                    return user;
                })
                .filter(user -> {
                    boolean valid = PasswordUtil.verify(password, user.getPasswordHash());
                    System.out.println("Password verification result for user '" + username + "': " + valid);
                    return valid;
                });
    }
}