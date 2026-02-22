package com.example.hotelreservationsystem.dao.impl;

import com.example.hotelreservationsystem.dao.UserDAO;
import com.example.hotelreservationsystem.exception.DatabaseException;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.util.DBConnection;

import java.sql.*;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    private static final String FIND_BY_USERNAME =
            "SELECT id, first_name, last_name, username, password_hash, role, created_at, updated_at " +
                    "FROM users WHERE username = ?";

    private static final String FIND_BY_ID =
            "SELECT id, first_name, last_name, username, password_hash, role, created_at, updated_at " +
                    "FROM users WHERE id = ?";

    private static final String INSERT_USER =
            "INSERT INTO users (first_name, last_name, username, password_hash, role) " +
                    "VALUES (?, ?, ?, ?, ?)";

    @Override
    public Optional<User> findByUsername(String username) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(FIND_BY_USERNAME)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch user by username", e);
        }
    }

    @Override
    public Optional<User> findById(int id) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(FIND_BY_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch user by id", e);
        }
    }

    @Override
    public int createUser(User user) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     INSERT_USER,
                     Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getRole() == null ? "RECEPTIONIST" : user.getRole());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new DatabaseException("User creation failed. No ID returned.", null);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to create user", e);
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {

        return new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("username"),
                null,
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getString("created_at"),
                rs.getString("updated_at")
        );
    }
}