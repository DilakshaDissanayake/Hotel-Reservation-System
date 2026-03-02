package com.example.hotelreservationsystem.service.impl;

import com.example.hotelreservationsystem.dao.UserDAO;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.service.UserService;
import com.example.hotelreservationsystem.util.PasswordUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public Optional<User> authenticate(String username, String password) {
        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {
            return Optional.empty();
        }
        return userDAO.findByUsername(username.trim())
                .filter(user -> PasswordUtil.verify(password, user.getPasswordHash()));
    }

    @Override
    public Optional<String> createPasswordResetToken(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        Optional<User> userOptional = userDAO.findByEmail(email.trim());
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        String rawToken = UUID.randomUUID().toString() + UUID.randomUUID();
        String tokenHash = sha256(rawToken);
        Timestamp expiresAt = Timestamp.from(Instant.now().plus(30, ChronoUnit.MINUTES));

        userDAO.savePasswordResetToken(userOptional.get().getId(), tokenHash, expiresAt);
        return Optional.of(rawToken);
    }

    @Override
    public boolean isResetTokenValid(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return false;
        }

        String tokenHash = sha256(rawToken.trim());
        return userDAO.findUserIdByValidResetToken(tokenHash).isPresent();
    }

    @Override
    public boolean resetPassword(String rawToken, String newPassword) {
        if (rawToken == null || rawToken.isBlank() || newPassword == null || newPassword.isBlank()) {
            return false;
        }

        if (newPassword.trim().length() < 8) {
            return false;
        }

        String tokenHash = sha256(rawToken.trim());
        String hashedPassword = PasswordUtil.hash(newPassword.trim());
        return userDAO.resetPasswordByValidToken(tokenHash, hashedPassword);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : hash) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available", e);
        }
    }
}