package com.example.balanceportal.config;

public final class AppConfig {
    public static final String SESSION_CUSTOMER_ID = "customerId";

    private AppConfig() {
    }

    public static String dbUrl() {
        return env("BALANCE_PORTAL_DB_URL", "jdbc:mariadb://localhost:3306/balance_portal");
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

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }
}
