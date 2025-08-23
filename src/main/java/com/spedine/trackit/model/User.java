package com.spedine.trackit.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private UUID id;

    private String name;

    private String email;

    private String password;

    private LocalDateTime createdAt;

    private User() {
    }

    public User(String name, String email, String password) {
        this(null, name, email, password, null);
    }

    public User(UUID id, String name, String email, String password, LocalDateTime createdAt) {
        this.id = id != null ? id : UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
