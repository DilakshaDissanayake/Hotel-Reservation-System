package com.example.hotelreservationsystem.dao.impl;

import com.example.hotelreservationsystem.dao.UserDAO;
import com.example.hotelreservationsystem.exception.DatabaseException;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.util.DBConnection;

import java.sql.*;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    private static final String FIND_BY_USERNAME =
            "SELECT id, first_name, last_name, username, email, password_hash, role, created_at, updated_at " +
                    "FROM users WHERE username = ?";

        private static final String FIND_BY_EMAIL =
            "SELECT id, first_name, last_name, username, email, password_hash, role, created_at, updated_at " +
                "FROM users WHERE email = ?";

    private static final String FIND_BY_ID =
            "SELECT id, first_name, last_name, username, email, password_hash, role, created_at, updated_at " +
                    "FROM users WHERE id = ?";

    private static final String INSERT_USER =
            "INSERT INTO users (first_name, last_name, username, email, password_hash, role) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        private static final String INSERT_PASSWORD_RESET_TOKEN =
            "INSERT INTO password_reset_tokens (user_id, token_hash, expires_at) VALUES (?, ?, ?)";

        private static final String FIND_USER_ID_BY_VALID_RESET_TOKEN =
            "SELECT user_id FROM password_reset_tokens " +
                "WHERE token_hash = ? AND used_at IS NULL AND expires_at > NOW() LIMIT 1";

        private static final String FIND_USER_ID_BY_VALID_RESET_TOKEN_FOR_UPDATE =
            "SELECT user_id FROM password_reset_tokens " +
                "WHERE token_hash = ? AND used_at IS NULL AND expires_at > NOW() LIMIT 1 FOR UPDATE";

        private static final String MARK_RESET_TOKEN_USED =
            "UPDATE password_reset_tokens SET used_at = NOW() WHERE token_hash = ? AND used_at IS NULL";

        private static final String UPDATE_PASSWORD_HASH_BY_USER_ID =
            "UPDATE users SET password_hash = ? WHERE id = ?";

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
    public Optional<User> findByEmail(String email) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(FIND_BY_EMAIL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch user by email", e);
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
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPasswordHash());
            ps.setString(6, user.getRole() == null ? "RECEPTIONIST" : user.getRole());

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

    @Override
    public void savePasswordResetToken(int userId, String tokenHash, Timestamp expiresAt) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_PASSWORD_RESET_TOKEN)) {

            ps.setInt(1, userId);
            ps.setString(2, tokenHash);
            ps.setTimestamp(3, expiresAt);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to store password reset token", e);
        }
    }

    @Override
    public Optional<Integer> findUserIdByValidResetToken(String tokenHash) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(FIND_USER_ID_BY_VALID_RESET_TOKEN)) {

            ps.setString(1, tokenHash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt("user_id"));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to validate password reset token", e);
        }
    }

    @Override
    public void markPasswordResetTokenUsed(String tokenHash) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(MARK_RESET_TOKEN_USED)) {

            ps.setString(1, tokenHash);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to consume password reset token", e);
        }
    }

    @Override
    public void updatePasswordHashByUserId(int userId, String passwordHash) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_PASSWORD_HASH_BY_USER_ID)) {

            ps.setString(1, passwordHash);
            ps.setInt(2, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to update password", e);
        }
    }

    @Override
    public boolean resetPasswordByValidToken(String tokenHash, String passwordHash) {
        try (Connection con = DBConnection.getConnection()) {
            boolean oldAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);

            try (PreparedStatement findTokenPs = con.prepareStatement(FIND_USER_ID_BY_VALID_RESET_TOKEN_FOR_UPDATE);
                 PreparedStatement updatePasswordPs = con.prepareStatement(UPDATE_PASSWORD_HASH_BY_USER_ID);
                 PreparedStatement markTokenUsedPs = con.prepareStatement(MARK_RESET_TOKEN_USED)) {

                findTokenPs.setString(1, tokenHash);

                Integer userId = null;
                try (ResultSet rs = findTokenPs.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("user_id");
                    }
                }

                if (userId == null) {
                    con.rollback();
                    return false;
                }

                updatePasswordPs.setString(1, passwordHash);
                updatePasswordPs.setInt(2, userId);
                int updatedUsers = updatePasswordPs.executeUpdate();

                markTokenUsedPs.setString(1, tokenHash);
                int updatedTokens = markTokenUsedPs.executeUpdate();

                if (updatedUsers == 1 && updatedTokens == 1) {
                    con.commit();
                    return true;
                }

                con.rollback();
                return false;

            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(oldAutoCommit);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to reset password with token", e);
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {

        return new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getString("created_at"),
                rs.getString("updated_at")
        );
    }
}