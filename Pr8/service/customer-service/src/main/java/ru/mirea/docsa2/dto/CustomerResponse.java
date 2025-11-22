package ru.mirea.docsa2.dto;

import ru.mirea.docsa2.model.Customer;

public record CustomerResponse(
    Long id,
    Long userId,
    String name,
    String phone,
    String address
) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
            customer.getId(),
            customer.getUserId(),
            customer.getName(),
            customer.getPhone(),
            customer.getAddress()
        );
    }
}

