package com.example.poll_system.domain.entities;

import java.time.LocalDateTime;

import com.example.poll_system.domain.exceptions.FieldIsRequiredException;

public class Vote {
    private String id;
    private String userId;
    private String pollOptionId;
    private LocalDateTime createdAt;

    public Vote(String id, String userId, String pollOptionId) {
        this.id = id;
        this.userId = userId;
        this.pollOptionId = pollOptionId;
        this.createdAt = LocalDateTime.now();
        validate();
    }

    private void validate() {
        if (id == null || id.isEmpty()) {
            throw new FieldIsRequiredException("id is required");
        }
        if (userId == null || userId.isEmpty()) {
            throw new FieldIsRequiredException("userId is required");
        }
        if (pollOptionId == null || pollOptionId.isEmpty()) {
            throw new FieldIsRequiredException("pollOptionId is required");
        }
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getPollOptionId() {
        return pollOptionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
