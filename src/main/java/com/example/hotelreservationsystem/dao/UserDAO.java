package com.example.hotelreservationsystem.dao;

import com.example.hotelreservationsystem.model.User;

import java.util.Optional;

public interface UserDAO {

    Optional<User> findByUsername(String username);

    Optional<User> findById(int id);

    int createUser(User user);
}
