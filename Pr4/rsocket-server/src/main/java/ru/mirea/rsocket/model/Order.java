package ru.mirea.rsocket.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("orders")
public class Order {
    @Id
    Long id;
    String customer;
    String address;
    String items;
    String status;
    Instant createdAt;
}
