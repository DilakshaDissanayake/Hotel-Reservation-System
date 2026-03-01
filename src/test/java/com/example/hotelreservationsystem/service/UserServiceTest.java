package com.example.hotelreservationsystem.service;

import com.example.hotelreservationsystem.dao.impl.UserDAOImpl;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@Disabled("Disabled in CI – requires real database")
class UserServiceTest {
        @Test
        void authenticate_shouldReturnUser_whenCredentialsValid() {

            UserService userService =
                    new UserServiceImpl(new UserDAOImpl());

            Optional<User> user =
                    userService.authenticate("DilakshaH", "11223344");

            assertTrue(user.isPresent());
        }
}