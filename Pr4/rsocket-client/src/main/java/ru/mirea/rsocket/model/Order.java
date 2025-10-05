package ru.mirea.rsocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    Long id;
    String customer;
    String address;
    String items;
    String status;
    Instant createdAt;
}
