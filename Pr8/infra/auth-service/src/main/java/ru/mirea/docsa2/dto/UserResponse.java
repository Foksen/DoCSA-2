package ru.mirea.docsa2.dto;

import ru.mirea.docsa2.model.User;

public record UserResponse(
    Long id,
    String username,
    String email
) {
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail()
        );
    }
}

