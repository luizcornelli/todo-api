package com.todo_api.dto;

public record LoginResponse(
        String token,
        String tokenType
) {
    public LoginResponse(String token) {
        this(token, "Bearer");
    }
}
