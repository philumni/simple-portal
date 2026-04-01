package com.example.balanceportal.config;

public final class AppConfig {
    public static final String SESSION_CUSTOMER_ID = "customerId";

    private AppConfig() {
    }

    public static String dbUrl() {
        return env(
                "BALANCE_PORTAL_DB_URL",
                "jdbc:mysql://localhost:3306/balance_portal?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
        );
    }

    public static String dbName() {
        return env("BALANCE_PORTAL_DB_NAME", "balance_portal");
    }

    public static String dbUser() {
        return env("BALANCE_PORTAL_DB_USER", "root");
    }

    public static String dbPassword() {
        return env("BALANCE_PORTAL_DB_PASSWORD", "password");
    }

    public static String appName() {
        return env("BALANCE_PORTAL_APP_NAME", "Northwind Balance Portal");
    }

    public static String instanceConnectionName() {
        return env("BALANCE_PORTAL_INSTANCE_CONNECTION_NAME", "");
    }

    public static String dbIpTypes() {
        return env("BALANCE_PORTAL_DB_IP_TYPES", "PUBLIC");
    }

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }
}
