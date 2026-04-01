package com.example.balanceportal.db;

import com.example.balanceportal.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {
    private static final String CONNECTOR_URL_TEMPLATE =
            "jdbc:mysql:///%s?socketFactory=com.google.cloud.sql.mysql.SocketFactory"
                    + "&cloudSqlInstance=%s"
                    + "&ipTypes=%s"
                    + "&cloudSqlRefreshStrategy=lazy";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("MySQL JDBC driver not found on the classpath.", exception);
        }
    }

    private ConnectionFactory() {
    }

    public static Connection openConnection() throws SQLException {
        String instanceConnectionName = AppConfig.instanceConnectionName();

        if (instanceConnectionName != null && !instanceConnectionName.isBlank()) {
            String connectorUrl = String.format(
                    CONNECTOR_URL_TEMPLATE,
                    AppConfig.dbName(),
                    instanceConnectionName,
                    AppConfig.dbIpTypes()
            );

            return DriverManager.getConnection(connectorUrl, AppConfig.dbUser(), AppConfig.dbPassword());
        }

        return DriverManager.getConnection(AppConfig.dbUrl(), AppConfig.dbUser(), AppConfig.dbPassword());
    }
}
