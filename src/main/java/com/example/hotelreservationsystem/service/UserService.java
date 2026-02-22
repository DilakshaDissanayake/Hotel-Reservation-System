package com.example.hotelreservationsystem.service;

import com.example.hotelreservationsystem.model.User;
import java.util.Optional;

public interface UserService {
    Optional<User> authenticate(String username, String password);
}
