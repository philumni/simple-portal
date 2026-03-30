package com.example.balanceportal.web;

import com.example.balanceportal.dao.TransactionDao;
import com.example.balanceportal.model.Transaction;
import com.example.balanceportal.security.SessionAuth;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/transactions")
public class TransactionsServlet extends BaseApiServlet {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private final TransactionDao transactionDao = new TransactionDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long customerId = SessionAuth.currentCustomerId(request);
        if (customerId == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "You are not signed in.");
            return;
        }

        try {
            List<Transaction> transactions = transactionDao.findByCustomerId(customerId);
            LedgerSummary summary = summarize(transactions);

            List<Map<String, Object>> rows = transactions.stream()
                    .map(transaction -> {
                        Map<String, Object> row = new LinkedHashMap<>();
                        row.put("id", transaction.id());
                        row.put("entryType", transaction.entryType());
                        row.put("description", transaction.description());
                        row.put("amount", scale(transaction.amount()));
                        row.put("postedAt", transaction.postedAt().format(DATE_FORMAT));
                        return row;
                    })
                    .toList();

            writeJson(response, HttpServletResponse.SC_OK, Map.of(
                    "ok", true,
                    "summary", Map.of(
                            "balanceOwed", scale(summary.balanceOwed()),
                            "totalCharges", scale(summary.totalCharges()),
                            "totalPayments", scale(summary.totalPayments())
                    ),
                    "transactions", rows
            ));
        } catch (Exception exception) {
            getServletContext().log("Could not load transactions for customer: " + customerId, exception);
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not load transactions.");
        }
    }

    private LedgerSummary summarize(List<Transaction> transactions) {
        BigDecimal balance = BigDecimal.ZERO;
        BigDecimal charges = BigDecimal.ZERO;
        BigDecimal payments = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            BigDecimal amount = transaction.amount();
            balance = balance.add(amount);

            if (amount.signum() >= 0) {
                charges = charges.add(amount);
            } else {
                payments = payments.add(amount.abs());
            }
        }

        return new LedgerSummary(balance, charges, payments);
    }

    private double scale(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private record LedgerSummary(BigDecimal balanceOwed, BigDecimal totalCharges, BigDecimal totalPayments) {
    }
}
