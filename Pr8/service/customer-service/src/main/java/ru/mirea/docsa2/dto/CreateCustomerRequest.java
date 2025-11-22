package ru.mirea.docsa2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCustomerRequest(
    @NotNull(message = "User ID is required")
    Long userId,

    @NotBlank(message = "Customer name is required")
    String name,

    @NotBlank(message = "Phone is required")
    String phone,

    String address
) {}

