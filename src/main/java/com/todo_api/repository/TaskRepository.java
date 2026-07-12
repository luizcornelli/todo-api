package com.todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todo_api.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
