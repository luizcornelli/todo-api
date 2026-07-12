package com.todo_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todo_api.dto.TaskRequest;
import com.todo_api.dto.TaskResponse;
import com.todo_api.entity.Task;
import com.todo_api.entity.enums.Priority;
import com.todo_api.entity.enums.Status;
import com.todo_api.security.UserPrincipal;
import com.todo_api.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@AuthenticationPrincipal UserPrincipal principal,
                                                @Valid @RequestBody TaskRequest request) {
        Task task = taskService.create(principal.getUser(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskResponse.fromEntity(task));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> findAll(@AuthenticationPrincipal UserPrincipal principal,
                                                        @RequestParam(required = false) Status status,
                                                        @RequestParam(required = false) Priority priority) {
        List<TaskResponse> tasks = taskService.findAll(principal.getUser(), status, priority).stream()
                .map(TaskResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findById(@AuthenticationPrincipal UserPrincipal principal,
                                                  @PathVariable Long id) {
        Task task = taskService.findById(principal.getUser(), id);
        return ResponseEntity.ok(TaskResponse.fromEntity(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@AuthenticationPrincipal UserPrincipal principal,
                                                @PathVariable Long id,
                                                @Valid @RequestBody TaskRequest request) {
        Task task = taskService.update(principal.getUser(), id, request);
        return ResponseEntity.ok(TaskResponse.fromEntity(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserPrincipal principal,
                                        @PathVariable Long id) {
        taskService.delete(principal.getUser(), id);
        return ResponseEntity.noContent().build();
    }
}
