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
import java.io.File;
import java.net.URLConnection;
import java.nio.file.AccessDeniedException;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailServiceImpl.class.getName());

    static {
        System.setProperty("jakarta.mail.util.StreamProvider",
                "org.eclipse.angus.mail.util.MailStreamProvider");

        try {
            URLConnection.setDefaultUseCaches("jar", false);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to disable JAR caching", e);
        }

        try {
            File tmpDir = new File(System.getProperty("java.io.tmpdir"));
            if (!tmpDir.canWrite()) {
                String userTmp = System.getProperty("user.home") + File.separator + ".tomcat-tmp";
                File fallback = new File(userTmp);
                if (!fallback.exists()) {
                    fallback.mkdirs();
                }
                System.setProperty("java.io.tmpdir", userTmp);
                LOGGER.info("Redirected java.io.tmpdir to writable location: " + userTmp);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to check/set java.io.tmpdir", e);
        }
    }

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
            LOGGER.info("Email sent successfully to: " + to);
        } catch (MessagingException e) {
            String errorMessage = "Failed to send email to " + to + ". ";
            if (hasAccessDeniedCause(e)) {
                errorMessage += "Tomcat temp dir not writable. Set CATALINA_TMPDIR to a writable folder.";
                LOGGER.log(Level.SEVERE, errorMessage, e);
            } else if (e.getMessage() != null && e.getMessage().contains("AuthenticationFailedException")) {
                errorMessage += "SMTP Authentication failed. Check MAIL_USERNAME and MAIL_PASSWORD in .env";
                LOGGER.log(Level.SEVERE, errorMessage);
            } else {
                errorMessage += "Check SMTP host, port, and network connectivity.";
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new RuntimeException(errorMessage, e);
        } catch (ServiceConfigurationError e) {
            LOGGER.log(Level.SEVERE, "Mail service configuration error", e);
            throw new RuntimeException("Mail service configuration error", e);
        }
    }

    private boolean hasAccessDeniedCause(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof AccessDeniedException || 
                (current.getMessage() != null && current.getMessage().contains("Access is denied"))) {
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

    @Override
    public void sendBillEmail(String to, String billBody) {
        String subject = "Ocean View Resort - Reservation Invoice";
        sendEmail(to, subject, billBody);
    }
}