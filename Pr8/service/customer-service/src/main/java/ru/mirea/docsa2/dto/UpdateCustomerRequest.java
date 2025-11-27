package ru.mirea.docsa2.dto;

public record UpdateCustomerRequest(
    String name,

    String phone,

    String address
) {}

