package com.example.hotelreservationsystem.util;

import com.example.hotelreservationsystem.exception.DatabaseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {

    private static final HikariDataSource dataSource;

    static {
        try {

            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            String url = getEnv(dotenv, "DB_URL");
            String username = getEnv(dotenv, "DB_USERNAME");
//            String password = getEnv(dotenv, "DB_PASSWORD");
            String driver = getEnv(dotenv, "DB_DRIVER");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(username);
//            config.setPassword(password);
            config.setDriverClassName(driver);

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setPoolName("HotelHikariPool");

            dataSource = new HikariDataSource(config);

        } catch (Exception e) {
            throw new DatabaseException("Failed to initialize datasource", e);
        }
    }

    private static String getEnv(Dotenv dotenv, String key) {
        String value = dotenv.get(key);
        if (value == null || value.isBlank()) {
            value = System.getenv(key);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + key);
        }
        return value;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}