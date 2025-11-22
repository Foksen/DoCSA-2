package ru.mirea.docsa2.dto;

import ru.mirea.docsa2.model.Product;
import java.math.BigDecimal;

public record ProductResponse(
    Long id,
    String name,
    BigDecimal price,
    Integer quantity
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getQuantity()
        );
    }
}

