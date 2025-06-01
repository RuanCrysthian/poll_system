package com.example.poll_system.infrastructure.persistence.jpa.entities;

import java.time.LocalDateTime;

import com.example.poll_system.domain.enums.VoteStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "votes")
public class VoteEntity {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "poll_option_id", nullable = false)
    private String pollOptionId;

    @Column(name = "poll_id", nullable = false)
    private String pollId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteStatus status;

    public VoteEntity() {
    }

    public VoteEntity(String id, String userId, String pollOptionId, String pollId,
            LocalDateTime createdAt, VoteStatus status) {
        this.id = id;
        this.userId = userId;
        this.pollOptionId = pollOptionId;
        this.pollId = pollId;
        this.createdAt = createdAt;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPollOptionId() {
        return pollOptionId;
    }

    public void setPollOptionId(String pollOptionId) {
        this.pollOptionId = pollOptionId;
    }

    public String getPollId() {
        return pollId;
    }

    public void setPollId(String pollId) {
        this.pollId = pollId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public VoteStatus getStatus() {
        return status;
    }

    public void setStatus(VoteStatus status) {
        this.status = status;
    }

}
