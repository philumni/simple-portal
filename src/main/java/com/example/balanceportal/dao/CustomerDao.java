package com.example.balanceportal.dao;

import com.example.balanceportal.db.ConnectionFactory;
import com.example.balanceportal.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public class CustomerDao {
    public Optional<Customer> findByEmail(String email) throws SQLException {
        String sql = """
                select id, email, full_name, password_hash, created_at
                from customers
                where email = ?
                """;

        try (Connection connection = ConnectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapCustomer(resultSet)) : Optional.empty();
            }
        }
    }

    public Optional<Customer> findById(long id) throws SQLException {
        String sql = """
                select id, email, full_name, password_hash, created_at
                from customers
                where id = ?
                """;

        try (Connection connection = ConnectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapCustomer(resultSet)) : Optional.empty();
            }
        }
    }

    public Customer createCustomer(String email, String fullName, String passwordHash) throws SQLException {
        String sql = """
                insert into customers (email, full_name, password_hash)
                values (?, ?, ?)
                """;

        try (Connection connection = ConnectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, email);
            statement.setString(2, fullName);
            statement.setString(3, passwordHash);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Customer insert did not return a generated id");
                }

                long id = keys.getLong(1);
                return new Customer(id, email, fullName, passwordHash, LocalDateTime.now());
            }
        }
    }

    private Customer mapCustomer(ResultSet resultSet) throws SQLException {
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        return new Customer(
                resultSet.getLong("id"),
                resultSet.getString("email"),
                resultSet.getString("full_name"),
                resultSet.getString("password_hash"),
                createdAt == null ? null : createdAt.toLocalDateTime()
        );
    }
}
