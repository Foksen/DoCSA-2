package ru.mirea.docsa2.dto;

public record CustomerDto(
    Long id,
    Long userId,
    String name,
    String phone,
    String address
) {}

