package com.ownsecurity.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Todos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Boolean completed;
    @ManyToMany(mappedBy = "todos", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<UserEntity> users = new ArrayList<>();
}
