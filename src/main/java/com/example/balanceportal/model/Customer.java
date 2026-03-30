package com.example.balanceportal.model;

import java.time.LocalDateTime;

public record Customer(
        long id,
        String email,
        String fullName,
        String passwordHash,
        LocalDateTime createdAt
) {
}
