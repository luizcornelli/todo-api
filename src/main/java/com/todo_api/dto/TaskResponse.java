package com.todo_api.dto;

import java.time.LocalDateTime;

import com.todo_api.entity.Task;
import com.todo_api.entity.enums.Priority;
import com.todo_api.entity.enums.Status;

public record TaskResponse(
        Long id,
        String title,
        String description,
        Priority priority,
        Status status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TaskResponse fromEntity(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
