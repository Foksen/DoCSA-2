package ru.mirea.docsa2.dto;

import jakarta.validation.constraints.Positive;

public record UpdateCustomerRequest(
    @Positive(message = "User ID must be positive")
    Long userId,

    String name,

    String phone,

    String address
) {}

