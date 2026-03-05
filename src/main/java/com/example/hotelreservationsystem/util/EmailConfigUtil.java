package com.example.hotelreservationsystem.util;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class EmailConfigUtil {

    private EmailConfigUtil() {
    }

    public static EmailConfig loadConfig() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        Properties classpathEnv = loadClasspathEnv();

        String smtpHost = getOptional(dotenv, classpathEnv, "MAIL_HOST", null);
        String smtpPort = getOptional(dotenv, classpathEnv, "MAIL_PORT", "587");
        String username = getOptional(dotenv, classpathEnv, "MAIL_USERNAME", null);
        String password = getOptional(dotenv, classpathEnv, "MAIL_PASSWORD", null);

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