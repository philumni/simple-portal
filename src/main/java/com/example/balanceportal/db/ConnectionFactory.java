package com.example.balanceportal.db;

import com.example.balanceportal.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {
    static {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("MariaDB JDBC driver not found on the classpath.", exception);
        }
    }

    private ConnectionFactory() {
    }

    public static Connection openConnection() throws SQLException {
        return DriverManager.getConnection(
                AppConfig.dbUrl(),
                AppConfig.dbUser(),
                AppConfig.dbPassword()
        );
    }
}
