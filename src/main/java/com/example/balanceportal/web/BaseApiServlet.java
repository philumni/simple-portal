package com.example.balanceportal.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public abstract class BaseApiServlet extends HttpServlet {
    protected static final Gson GSON = new GsonBuilder().serializeNulls().create();

    protected <T> T readJson(HttpServletRequest request, Class<T> type) throws IOException {
        try (BufferedReader reader = request.getReader()) {
            return GSON.fromJson(reader, type);
        }
    }

    protected void writeJson(HttpServletResponse response, int status, Object payload) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(GSON.toJson(payload));
    }

    protected void writeError(HttpServletResponse response, int status, String message) throws IOException {
        writeJson(response, status, Map.of("ok", false, "message", message));
    }
}
