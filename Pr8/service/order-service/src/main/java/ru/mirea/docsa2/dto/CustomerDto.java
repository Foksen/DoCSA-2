package ru.mirea.docsa2.dto;

import lombok.Data;

@Data
public class CustomerDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
}

