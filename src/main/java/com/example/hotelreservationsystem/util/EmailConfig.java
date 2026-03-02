package com.example.hotelreservationsystem.util;

public class EmailConfig {

    private final String smtpHost;
    private final String smtpPort;
    private final String username;
    private final String password;
    private final String fromAddress;
    private final boolean smtpAuth;
    private final boolean startTls;
    private final boolean sslEnable;

    public EmailConfig(String smtpHost,
                       String smtpPort,
                       String username,
                       String password,
                       String fromAddress,
                       boolean smtpAuth,
                       boolean startTls,
                       boolean sslEnable) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.username = username;
        this.password = password;
        this.fromAddress = fromAddress;
        this.smtpAuth = smtpAuth;
        this.startTls = startTls;
        this.sslEnable = sslEnable;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public boolean isSmtpAuth() {
        return smtpAuth;
    }

    public boolean isStartTls() {
        return startTls;
    }

    public boolean isSslEnable() {
        return sslEnable;
    }

    public void validate() {
        if (isBlank(smtpHost) || isBlank(smtpPort) || isBlank(username) || isBlank(password) || isBlank(fromAddress)) {
            throw new IllegalStateException("Mail configuration is missing. Set MAIL_HOST, MAIL_PORT, MAIL_USERNAME, and MAIL_PASSWORD.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}