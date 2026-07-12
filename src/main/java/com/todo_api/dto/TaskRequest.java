package com.todo_api.dto;

import com.todo_api.entity.enums.Priority;
import com.todo_api.entity.enums.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskRequest(

        @NotBlank(message = "title is required")
        String title,

        String description,

        @NotNull(message = "priority is required")
        Priority priority,

        @NotNull(message = "status is required")
        Status status
) {
}
