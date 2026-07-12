package com.todo_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todo_api.entity.Task;
import com.todo_api.entity.enums.Priority;
import com.todo_api.entity.enums.Status;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByUserId(Long userId);

    List<Task> findAllByUserIdAndStatus(Long userId, Status status);

    List<Task> findAllByUserIdAndPriority(Long userId, Priority priority);

    List<Task> findAllByUserIdAndStatusAndPriority(Long userId, Status status, Priority priority);

    Optional<Task> findByIdAndUserId(Long id, Long userId);
}
