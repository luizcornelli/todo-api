package com.todo_api.dto;

import com.todo_api.entity.User;

public record UserResponse(
        Long id,
        String name,
        String email
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
