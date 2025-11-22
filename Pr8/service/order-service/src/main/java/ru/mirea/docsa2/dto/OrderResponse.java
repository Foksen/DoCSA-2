package ru.mirea.docsa2.dto;

import ru.mirea.docsa2.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
    Long id,
    Long customerId,
    Long productId,
    Integer quantity,
    BigDecimal totalPrice,
    Order.OrderStatus status,
    LocalDateTime createdAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getCustomerId(),
            order.getProductId(),
            order.getQuantity(),
            order.getTotalPrice(),
            order.getStatus(),
            order.getCreatedAt()
        );
    }
}

