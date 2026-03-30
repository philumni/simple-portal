package com.example.balanceportal.web;

import com.example.balanceportal.config.AppConfig;
import com.example.balanceportal.dao.CustomerDao;
import com.example.balanceportal.model.Customer;
import com.example.balanceportal.security.SessionAuth;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

@WebServlet("/api/me")
public class CurrentCustomerServlet extends BaseApiServlet {
    private final CustomerDao customerDao = new CustomerDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long customerId = SessionAuth.currentCustomerId(request);
        if (customerId == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "You are not signed in.");
            return;
        }

        try {
            Optional<Customer> customer = customerDao.findById(customerId);
            if (customer.isEmpty()) {
                SessionAuth.signOut(request);
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Your session is no longer valid.");
                return;
            }

            writeJson(response, HttpServletResponse.SC_OK, Map.of(
                    "ok", true,
                    "appName", AppConfig.appName(),
                    "customer", Map.of(
                            "id", customer.get().id(),
                            "fullName", customer.get().fullName(),
                            "email", customer.get().email()
                    )
            ));
        } catch (SQLException exception) {
            getServletContext().log("Could not load current customer: " + customerId, exception);
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not load the current customer.");
        }
    }
}
