package ru.mirea.docsa2.dto;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record UpdateProductRequest(
    String name,

    @Positive(message = "Price must be positive")
    BigDecimal price,

    @Positive(message = "Quantity must be positive")
    Integer quantity
) {}

