package com.example.poll_system.infrastructure.persistence.jpa.mappers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.entities.PollOption;
import com.example.poll_system.infrastructure.persistence.jpa.entities.PollEntity;

@Component
public class PollMapper {

    @Autowired
    private PollOptionMapper pollOptionMapper;

    // Setter for unit testing
    public void setPollOptionMapper(PollOptionMapper pollOptionMapper) {
        this.pollOptionMapper = pollOptionMapper;
    }

    public PollEntity toEntity(Poll domainPoll) {
        if (domainPoll == null) {
            return null;
        }

        PollEntity entity = new PollEntity(
                domainPoll.getId(),
                domainPoll.getTitle(),
                domainPoll.getDescription(),
                domainPoll.getOwnerId(),
                domainPoll.getStartDate(),
                domainPoll.getEndDate(),
                domainPoll.getStatus());

        // Set options if they exist
        if (domainPoll.getOptions() != null) {
            entity.setOptions(pollOptionMapper.toEntityList(domainPoll.getOptions()));
        }

        return entity;
    }

    public Poll toDomain(PollEntity entity) {
        if (entity == null) {
            return null;
        }

        List<PollOption> options = pollOptionMapper.toDomainList(entity.getOptions());

        // Use fromDatabase method to avoid validation when reconstructing existing
        // polls
        return Poll.fromDatabase(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getOwnerId(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus(),
                options);
    }
}
