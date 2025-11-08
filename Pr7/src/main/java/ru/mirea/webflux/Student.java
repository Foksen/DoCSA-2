package ru.mirea.webflux;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("students")
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Student {
    @Id
    Long id;
    String name;
    int age;
}
