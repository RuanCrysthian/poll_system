package com.example.poll_system.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "poll_options")
public class PollOptionEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String description;

    @Column(name = "poll_id", nullable = false)
    private String pollId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", insertable = false, updatable = false)
    private PollEntity poll;

    public PollOptionEntity() {
    }

    public PollOptionEntity(String id, String description, String pollId) {
        this.id = id;
        this.description = description;
        this.pollId = pollId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPollId() {
        return pollId;
    }

    public void setPollId(String pollId) {
        this.pollId = pollId;
    }
}
