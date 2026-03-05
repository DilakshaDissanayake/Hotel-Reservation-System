package com.example.hotelreservationsystem.util;

import com.example.hotelreservationsystem.exception.DatabaseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static final HikariDataSource dataSource;

    static {
        try {

            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            Properties classpathEnv = loadClasspathEnv();

            String url = getEnv(dotenv, classpathEnv, "DB_URL");
            String username = getEnv(dotenv, classpathEnv, "DB_USERNAME");
            String password = getEnv(dotenv, classpathEnv, "DB_PASSWORD");
            String driver = getEnv(dotenv, classpathEnv, "DB_DRIVER");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName(driver);

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setPoolName("HotelHikariPool");

            dataSource = new HikariDataSource(config);

        } catch (Exception e) {
            System.err.println("CRITICAL: Failed to initialize Hotel Database Connection Pool!");
            System.err.println("Check your environment variables (DB_URL, DB_USERNAME, DB_PASSWORD, DB_DRIVER).");
            System.err.println("Error detail: " + e.getMessage());
            throw new DatabaseException("Failed to initialize datasource. Please check server logs for details.", e);
        }
    }

    private static Properties loadClasspathEnv() {
        Properties properties = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = DBConnection.class.getClassLoader();
        }
        try (InputStream input = classLoader.getResourceAsStream(".env")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException ignored) {
        }
        return properties;
    }

    private static String getEnv(Dotenv dotenv, Properties classpathEnv, String key) {
        String value = dotenv.get(key);
        if (value == null) {
            value = classpathEnv.getProperty(key);
        }
        if (value == null) {
            value = System.getenv(key);
        }

        if (value == null || (value.isBlank() && !"DB_PASSWORD".equals(key))) {
            throw new IllegalStateException("Missing required environment variable: " + key);
        }
        return value.trim();
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}