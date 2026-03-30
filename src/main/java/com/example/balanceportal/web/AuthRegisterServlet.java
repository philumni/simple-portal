package com.example.balanceportal.web;

import com.example.balanceportal.dao.CustomerDao;
import com.example.balanceportal.dao.TransactionDao;
import com.example.balanceportal.model.Customer;
import com.example.balanceportal.security.PasswordHasher;
import com.example.balanceportal.security.SessionAuth;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/api/auth/register")
public class AuthRegisterServlet extends BaseApiServlet {
    private final CustomerDao customerDao = new CustomerDao();
    private final TransactionDao transactionDao = new TransactionDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RegisterPayload payload = readJson(request, RegisterPayload.class);
        if (payload == null) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Request body is required.");
            return;
        }

        String fullName = clean(payload.fullName());
        String email = clean(payload.email()).toLowerCase();
        String password = payload.password() == null ? "" : payload.password().trim();

        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Name, email, and password are required.");
            return;
        }

        if (password.length() < 8) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Use at least 8 characters for the password.");
            return;
        }

        try {
            if (customerDao.findByEmail(email).isPresent()) {
                writeError(response, HttpServletResponse.SC_CONFLICT, "That email is already registered.");
                return;
            }

            Customer customer = customerDao.createCustomer(email, fullName, PasswordHasher.hash(password));
            transactionDao.createStarterLedger(customer.id());
            SessionAuth.signIn(request, customer.id());

            writeJson(response, HttpServletResponse.SC_CREATED, Map.of(
                    "ok", true,
                    "customer", Map.of(
                            "id", customer.id(),
                            "fullName", customer.fullName(),
                            "email", customer.email()
                    )
            ));
        } catch (SQLException exception) {
            getServletContext().log("Registration failed for email: " + email, exception);
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not create the account.");
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private record RegisterPayload(String fullName, String email, String password) {
    }
}
