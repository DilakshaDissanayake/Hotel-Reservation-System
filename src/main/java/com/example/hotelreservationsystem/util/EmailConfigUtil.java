package com.example.hotelreservationsystem.util;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class EmailConfigUtil {

    private EmailConfigUtil() {
    }

    public static EmailConfig loadConfig() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        Properties classpathEnv = loadClasspathEnv();

        String smtpHost = getRequired(dotenv, classpathEnv, "MAIL_HOST");
        String smtpPort = getRequired(dotenv, classpathEnv, "MAIL_PORT");
        String username = getRequired(dotenv, classpathEnv, "MAIL_USERNAME");
        String password = getRequired(dotenv, classpathEnv, "MAIL_PASSWORD");

        return new EmailConfig(
                smtpHost,
                smtpPort,
                username,
                password,
                getOptional(dotenv, classpathEnv, "MAIL_FROM", username),
                Boolean.parseBoolean(getOptional(dotenv, classpathEnv, "MAIL_AUTH", "true")),
                Boolean.parseBoolean(getOptional(dotenv, classpathEnv, "MAIL_STARTTLS", "true")),
                Boolean.parseBoolean(getOptional(dotenv, classpathEnv, "MAIL_SSL_ENABLE", "false"))
        );
    }


    private static Properties loadClasspathEnv() {
        Properties properties = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = EmailConfigUtil.class.getClassLoader();
        }

        try (InputStream input = classLoader.getResourceAsStream(".env")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException ignored) {
        }

        return properties;
    }

    private static boolean hasMailConfiguration(Dotenv dotenv, Properties classpathEnv) {
        return !isBlank(getOptional(dotenv, classpathEnv, "MAIL_HOST", null))
                || !isBlank(getOptional(dotenv, classpathEnv, "MAIL_PORT", null))
                || !isBlank(getOptional(dotenv, classpathEnv, "MAIL_USERNAME", null))
                || !isBlank(getOptional(dotenv, classpathEnv, "MAIL_PASSWORD", null));
    }

    private static String getRequired(Dotenv dotenv, Properties classpathEnv, String key) {
        String value = getOptional(dotenv, classpathEnv, key, null);
        if (isBlank(value)) {
            throw new IllegalStateException("Missing required environment variable: " + key);
        }
        return value;
    }

    private static String getOptional(Dotenv dotenv, Properties classpathEnv, String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            value = dotenv.get(key);
        }
        if (value == null || value.isBlank()) {
            value = classpathEnv.getProperty(key);
        }
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

}