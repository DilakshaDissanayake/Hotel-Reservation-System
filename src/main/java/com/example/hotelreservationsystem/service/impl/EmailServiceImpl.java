package com.example.hotelreservationsystem.service.impl;

import com.example.hotelreservationsystem.service.EmailService;
import com.example.hotelreservationsystem.util.EmailConfig;
import com.example.hotelreservationsystem.util.EmailConfigUtil;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.nio.file.AccessDeniedException;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailServiceImpl.class.getName());

    private final EmailConfig emailConfig;

    public EmailServiceImpl() {
        emailConfig = EmailConfigUtil.loadConfig();
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        emailConfig.validate();

        Properties props = new Properties();
        props.put("mail.smtp.auth", String.valueOf(emailConfig.isSmtpAuth()));
        props.put("mail.smtp.starttls.enable", String.valueOf(emailConfig.isStartTls()));
        props.put("mail.smtp.ssl.enable", String.valueOf(emailConfig.isSslEnable()));
        props.put("mail.smtp.host", emailConfig.getSmtpHost());
        props.put("mail.smtp.port", emailConfig.getSmtpPort());

        try {
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailConfig.getFromAddress()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            LOGGER.info("Email sent successfully");
        } catch (MessagingException | ServiceConfigurationError e) {
            if (hasCause(e)) {
                LOGGER.log(Level.SEVERE,
                        "Failed to send email because Tomcat temp directory is not writable. "
                                + "Set CATALINA_TMPDIR to a writable folder outside Program Files.",
                        e);
            }
            LOGGER.log(Level.SEVERE, "Failed to send email", e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private boolean hasCause(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof AccessDeniedException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {
        String subject = "Reset your password";
        String body = "We received a password reset request for your account. "
                + "Use the link below to set a new password (valid for 30 minutes):\n\n"
                + resetLink
                + "\n\nIf you did not request this, you can ignore this email.";
        sendEmail(to, subject, body);
    }
}