package com.example.poll_system.domain.entities.events;

import java.time.LocalDateTime;

import com.example.poll_system.domain.entities.DomainEvent;
import com.example.poll_system.domain.exceptions.FieldIsRequiredException;

public class PollClosedEvent extends DomainEvent {

    private final String pollId;
    private final String ownerPollId;
    private final String ownerEmail;
    private final String pollTitle;
    private final String pollDescription;
    private final LocalDateTime pollClosedDate;

    public PollClosedEvent(
            String pollId,
            String ownerPollId,
            String ownerEmail,
            String pollTitle,
            String pollDescription,
            LocalDateTime pollClosedDate) {
        super(PollClosedEvent.class.getSimpleName());
        this.pollId = pollId;
        this.ownerPollId = ownerPollId;
        this.ownerEmail = ownerEmail;
        this.pollTitle = pollTitle;
        this.pollDescription = pollDescription;
        this.pollClosedDate = pollClosedDate;
        this.validate();
    }

    private void validate() {
        if (pollId == null || pollId.trim().isEmpty()) {
            throw new FieldIsRequiredException("pollId is required");
        }
        if (ownerPollId == null || ownerPollId.trim().isEmpty()) {
            throw new FieldIsRequiredException("ownerPollId is required");
        }
        if (ownerEmail == null || ownerEmail.trim().isEmpty()) {
            throw new FieldIsRequiredException("ownerEmail is required");
        }
        if (pollTitle == null || pollTitle.trim().isEmpty()) {
            throw new FieldIsRequiredException("pollTitle is required");
        }
        if (pollDescription == null || pollDescription.trim().isEmpty()) {
            throw new FieldIsRequiredException("pollDescription is required");
        }
        if (pollClosedDate == null) {
            throw new FieldIsRequiredException("pollClosedDate is required");
        }
    }

    public String getPollId() {
        return pollId;
    }

    public String getOwnerPollId() {
        return ownerPollId;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getPollTitle() {
        return pollTitle;
    }

    public String getPollDescription() {
        return pollDescription;
    }

    public LocalDateTime getPollClosedDate() {
        return pollClosedDate;
    }
}
