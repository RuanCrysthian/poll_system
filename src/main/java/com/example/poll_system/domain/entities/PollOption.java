package com.example.poll_system.domain.entities;

import com.example.poll_system.domain.exceptions.FieldIsRequiredException;

public class PollOption {
    private String id;
    private String description;
    private String pollId;

    public PollOption(String id, String description, String pollId) {
        this.id = id;
        this.description = description;
        this.pollId = pollId;
        this.validate();
    }

    private void validate() {
        if (id == null || id.trim().isEmpty()) {
            throw new FieldIsRequiredException("id is required");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new FieldIsRequiredException("description is required");
        }
        if (pollId == null || pollId.trim().isEmpty()) {
            throw new FieldIsRequiredException("pollId is required");
        }
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getPollId() {
        return pollId;
    }

}
