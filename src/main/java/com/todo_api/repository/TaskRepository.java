package com.todo_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todo_api.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByUserId(Long userId);

    Optional<Task> findByIdAndUserId(Long id, Long userId);
}
