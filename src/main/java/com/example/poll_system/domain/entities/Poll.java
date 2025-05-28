package com.example.poll_system.domain.entities;

import java.time.LocalDateTime;
import java.util.List;

import com.example.poll_system.domain.enums.PollStatus;
import com.example.poll_system.domain.exceptions.BusinessRulesException;
import com.example.poll_system.domain.exceptions.FieldIsRequiredException;
import com.example.poll_system.domain.exceptions.InvalidDateTimeException;
import com.example.poll_system.domain.utils.DateUtils;

public class Poll {
    private String id;
    private String title;
    private String description;
    private String ownerId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private PollStatus status;
    private List<PollOption> options;

    private Poll(
            String id,
            String title,
            String description,
            String ownerId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            PollStatus status,
            List<PollOption> options) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.ownerId = ownerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.options = options;
        this.validate();
    }

    public static Poll createScheduledPoll(
            String id,
            String title,
            String description,
            String ownerId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<PollOption> options) {
        return new Poll(id, title, description, ownerId, startDate, endDate, PollStatus.SCHEDULED, options);
    }

    public static Poll createOpenPoll(
            String id,
            String title,
            String description,
            String ownerId,
            LocalDateTime endDate,
            List<PollOption> options) {
        return new Poll(id, title, description, ownerId, LocalDateTime.now().plusSeconds(1), endDate, PollStatus.OPEN,
                options);
    }

    private void validate() {
        if (id == null || id.trim().isEmpty()) {
            throw new FieldIsRequiredException("id is required");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new FieldIsRequiredException("title is required");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new FieldIsRequiredException("description is required");
        }
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new FieldIsRequiredException("ownerId is required");
        }
        if (startDate == null) {
            throw new FieldIsRequiredException("startDate is required");
        }
        if (endDate == null) {
            throw new FieldIsRequiredException("endDate is required");
        }
        if (options == null || options.isEmpty()) {
            throw new FieldIsRequiredException("options are required");
        }
        if (DateUtils.isDateInPast(startDate)) {
            throw new InvalidDateTimeException("startDate cannot be in the past");
        }
        if (!DateUtils.isDateInRange(startDate, endDate)) {
            throw new InvalidDateTimeException("startDate must be before endDate");
        }
        if (options.size() < 2) {
            throw new BusinessRulesException("should at least two options are required");
        }
        if (options.size() > 10) {
            throw new BusinessRulesException("should at most ten options are required");
        }
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public PollStatus getStatus() {
        return status;
    }

    public List<PollOption> getOptions() {
        return options;
    }

    public void setStatus(PollStatus status) {
        this.status = status;
    }

}
