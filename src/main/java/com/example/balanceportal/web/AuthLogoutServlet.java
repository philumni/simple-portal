package com.example.balanceportal.web;

import com.example.balanceportal.security.SessionAuth;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/auth/logout")
public class AuthLogoutServlet extends BaseApiServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionAuth.signOut(request);
        writeJson(response, HttpServletResponse.SC_OK, Map.of("ok", true));
    }
}
