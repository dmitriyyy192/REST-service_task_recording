package com.ownsecurity.repository;

import com.ownsecurity.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {
    Optional<TodoEntity> findByTitle(String title);
}
