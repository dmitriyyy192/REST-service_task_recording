package com.ownsecurity.dto;

import com.ownsecurity.entity.TodoEntity;

public class TodoDto {
    private Long id;
    private String title;
    private Boolean completed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TodoDto(String title, Boolean completed, Long id) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public static TodoDto toTodoDto(TodoEntity todo) {
        return new TodoDto(todo.getTitle(), todo.getCompleted(), todo.getId());
    }

}
