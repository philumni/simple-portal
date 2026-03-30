package com.example.balanceportal.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Transaction(
        long id,
        long customerId,
        String entryType,
        String description,
        BigDecimal amount,
        LocalDateTime postedAt
) {
}
