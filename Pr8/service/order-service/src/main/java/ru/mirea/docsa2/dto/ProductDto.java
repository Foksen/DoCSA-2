package ru.mirea.docsa2.dto;

import java.math.BigDecimal;

public record ProductDto(
    Long id,
    String name,
    BigDecimal price,
    Integer quantity
) {}

