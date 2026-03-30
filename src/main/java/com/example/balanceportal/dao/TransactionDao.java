package com.example.balanceportal.dao;

import com.example.balanceportal.db.ConnectionFactory;
import com.example.balanceportal.model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao {
    public List<Transaction> findByCustomerId(long customerId) throws SQLException {
        String sql = """
                select id, customer_id, entry_type, description, amount, posted_at
                from customer_transactions
                where customer_id = ?
                order by posted_at desc, id desc
                """;

        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = ConnectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, customerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    transactions.add(mapTransaction(resultSet));
                }
            }
        }

        return transactions;
    }

    public void createStarterLedger(long customerId) throws SQLException {
        String sql = """
                insert into customer_transactions (customer_id, entry_type, description, amount, posted_at)
                values
                (?, 'CHARGE', 'January invoice', 425.00, now() - interval 21 day),
                (?, 'PAYMENT', 'Card payment', -150.00, now() - interval 15 day),
                (?, 'CHARGE', 'Support retainer', 85.00, now() - interval 7 day),
                (?, 'ADJUSTMENT', 'Courtesy credit', -25.00, now() - interval 3 day)
                """;

        try (Connection connection = ConnectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, customerId);
            statement.setLong(2, customerId);
            statement.setLong(3, customerId);
            statement.setLong(4, customerId);
            statement.executeUpdate();
        }
    }

    private Transaction mapTransaction(ResultSet resultSet) throws SQLException {
        Timestamp postedAt = resultSet.getTimestamp("posted_at");
        return new Transaction(
                resultSet.getLong("id"),
                resultSet.getLong("customer_id"),
                resultSet.getString("entry_type"),
                resultSet.getString("description"),
                resultSet.getBigDecimal("amount"),
                postedAt == null ? null : postedAt.toLocalDateTime()
        );
    }
}
