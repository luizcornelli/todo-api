package com.todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todo_api.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
}
