package com.example.balanceportal.web;

import com.example.balanceportal.dao.CustomerDao;
import com.example.balanceportal.model.Customer;
import com.example.balanceportal.security.PasswordHasher;
import com.example.balanceportal.security.SessionAuth;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

@WebServlet("/api/auth/login")
public class AuthLoginServlet extends BaseApiServlet {
    private final CustomerDao customerDao = new CustomerDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LoginPayload payload = readJson(request, LoginPayload.class);
        if (payload == null) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Request body is required.");
            return;
        }

        String email = clean(payload.email()).toLowerCase();
        String password = payload.password() == null ? "" : payload.password().trim();

        if (email.isBlank() || password.isBlank()) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Email and password are required.");
            return;
        }

        try {
            Optional<Customer> customer = customerDao.findByEmail(email);
            if (customer.isEmpty() || !PasswordHasher.matches(password, customer.get().passwordHash())) {
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Email or password is incorrect.");
                return;
            }

            SessionAuth.signIn(request, customer.get().id());
            writeJson(response, HttpServletResponse.SC_OK, Map.of(
                    "ok", true,
                    "customer", Map.of(
                            "id", customer.get().id(),
                            "fullName", customer.get().fullName(),
                            "email", customer.get().email()
                    )
            ));
        } catch (SQLException exception) {
            getServletContext().log("Login failed for email: " + email, exception);
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not sign in.");
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private record LoginPayload(String email, String password) {
    }
}
