package com.example.hotelreservationsystem.util;

import com.example.hotelreservationsystem.exception.DatabaseException;
import java.sql.Connection;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.SQLException;

public class DBConnection {
    private static final HikariDataSource dataSource;
    static {
        try {
            Dotenv dotenv = Dotenv.load();
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dotenv.get("DB_URL"));
            config.setUsername(dotenv.get("DB_USERNAME"));
            config.setPassword(dotenv.get("DB_PASSWORD"));
            config.setDriverClassName(dotenv.get("DB_DRIVER"));
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new DatabaseException("Failed to initialize datasource", e);
        }
    }
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}

