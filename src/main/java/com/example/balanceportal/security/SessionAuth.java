package com.example.balanceportal.security;

import com.example.balanceportal.config.AppConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class SessionAuth {
    private SessionAuth() {
    }

    public static Long currentCustomerId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object value = session.getAttribute(AppConfig.SESSION_CUSTOMER_ID);
        return value instanceof Long customerId ? customerId : null;
    }

    public static void signIn(HttpServletRequest request, long customerId) {
        HttpSession session = request.getSession(true);
        session.setAttribute(AppConfig.SESSION_CUSTOMER_ID, customerId);
    }

    public static void signOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
