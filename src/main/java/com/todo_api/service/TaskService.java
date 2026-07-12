package com.todo_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.todo_api.dto.TaskRequest;
import com.todo_api.entity.Task;
import com.todo_api.entity.User;
import com.todo_api.entity.enums.Priority;
import com.todo_api.entity.enums.Status;
import com.todo_api.exception.TaskNotFoundException;
import com.todo_api.repository.TaskRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task create(User user, TaskRequest request) {
        Task task = new Task();
        task.setUser(user);
        applyRequest(task, request);
        return taskRepository.save(task);
    }

    public List<Task> findAll(User user, Status status, Priority priority) {
        Long userId = user.getId();

        if (status != null && priority != null) {
            return taskRepository.findAllByUserIdAndStatusAndPriority(userId, status, priority);
        }
        if (status != null) {
            return taskRepository.findAllByUserIdAndStatus(userId, status);
        }
        if (priority != null) {
            return taskRepository.findAllByUserIdAndPriority(userId, priority);
        }
        return taskRepository.findAllByUserId(userId);
    }

    public Task findById(User user, Long id) {
        return taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task update(User user, Long id, TaskRequest request) {
        Task task = findById(user, id);
        applyRequest(task, request);
        return taskRepository.save(task);
    }

    public void delete(User user, Long id) {
        Task task = findById(user, id);
        taskRepository.delete(task);
    }

    private void applyRequest(Task task, TaskRequest request) {
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setPriority(request.priority());
        task.setStatus(request.status());
    }
}
