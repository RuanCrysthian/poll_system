package com.example.poll_system.infrastructure.persistence.jpa.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.poll_system.domain.entities.PollOption;
import com.example.poll_system.infrastructure.persistence.jpa.entities.PollOptionEntity;

@Component
public class PollOptionMapper {

    public PollOptionEntity toEntity(PollOption domainPollOption) {
        if (domainPollOption == null) {
            return null;
        }

        PollOptionEntity entity = new PollOptionEntity();
        entity.setId(domainPollOption.getId());
        entity.setDescription(domainPollOption.getDescription());
        entity.setPollId(domainPollOption.getPollId());
        return entity;
    }

    public PollOption toDomain(PollOptionEntity entity) {
        if (entity == null) {
            return null;
        }

        return new PollOption(
                entity.getId(),
                entity.getDescription(), // Use getText() instead of getDescription()
                entity.getPollId());
    }

    public List<PollOptionEntity> toEntityList(List<PollOption> domainOptions) {
        if (domainOptions == null) {
            return null;
        }

        return domainOptions.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public List<PollOption> toDomainList(List<PollOptionEntity> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
}
