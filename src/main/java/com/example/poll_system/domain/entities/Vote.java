package com.example.poll_system.domain.entities;

import java.time.LocalDateTime;

import com.example.poll_system.domain.enums.VoteStatus;
import com.example.poll_system.domain.exceptions.FieldIsRequiredException;

public class Vote {
    private String id;
    private String userId;
    private String pollOptionId;
    private LocalDateTime createdAt;
    private VoteStatus status;

    public Vote(
            String id,
            String userId,
            String pollOptionId,
            LocalDateTime createdAt,
            VoteStatus status) {
        this.id = id;
        this.userId = userId;
        this.pollOptionId = pollOptionId;
        this.createdAt = createdAt;
        this.status = status;
        validate();
    }

    private void validate() {
        if (id == null || id.trim().isEmpty()) {
            throw new FieldIsRequiredException("id is required");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new FieldIsRequiredException("userId is required");
        }
        if (pollOptionId == null || pollOptionId.trim().isEmpty()) {
            throw new FieldIsRequiredException("pollOptionId is required");
        }
        if (createdAt == null) {
            throw new FieldIsRequiredException("createdAt is required");
        }
        if (status == null) {
            throw new FieldIsRequiredException("status is required");
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

    public VoteStatus getStatus() {
        return status;
    }

    public void setStatus(VoteStatus status) {
        this.status = status;
    }

}
