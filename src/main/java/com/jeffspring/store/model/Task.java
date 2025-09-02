package com.jeffspring.store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "tasks")
public class Task {
    public enum Priority {LOW,MEDIUM,HIGH}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank
    @Column(nullable = false,length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(name = "created_by",nullable = false)
    private String createdBy;

    @Column(name = "created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "due_date",nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dueDate;


    // Getters and Setters
    public long getId(){
        return id;
    }
    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }
    public Priority getPriority(){
        return priority;
    }
    public boolean isCompleted(){
        return completed;
    }
    public String getCreatedBy(){
        return createdBy;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public LocalDateTime getDueDate(){
        return dueDate;
    }

    public void setId(long id) {
        this.id = id;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public void setPriority(Priority priority){
        this.priority = priority;
    }
    public void setCompleted(boolean completed){
        this.completed = completed;
    }
    public void setCreatedBy(String createdBy){
        this.createdBy = createdBy;
    }
    // Make sure that the created_at attribute is getting generated in a lifecycle
    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
    }
    public void setDueDate(LocalDateTime due_date){
        this.dueDate = due_date;
    }
}
