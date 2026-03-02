package com.example.hotelreservationsystem.dao;

import com.example.hotelreservationsystem.model.User;

import java.sql.Timestamp;
import java.util.Optional;

public interface UserDAO {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findById(int id);

    int createUser(User user);

    void savePasswordResetToken(int userId, String tokenHash, Timestamp expiresAt);

    Optional<Integer> findUserIdByValidResetToken(String tokenHash);

    void markPasswordResetTokenUsed(String tokenHash);

    void updatePasswordHashByUserId(int userId, String passwordHash);

    boolean resetPasswordByValidToken(String tokenHash, String passwordHash);
}
