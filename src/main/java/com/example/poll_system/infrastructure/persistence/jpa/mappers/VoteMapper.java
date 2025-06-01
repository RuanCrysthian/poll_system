package com.example.poll_system.infrastructure.persistence.jpa.mappers;

import org.springframework.stereotype.Component;

import com.example.poll_system.domain.entities.Vote;
import com.example.poll_system.infrastructure.persistence.jpa.entities.VoteEntity;

@Component
public class VoteMapper {

    public VoteEntity toEntity(Vote domainVote) {
        if (domainVote == null) {
            return null;
        }

        return new VoteEntity(
                domainVote.getId(),
                domainVote.getUserId(),
                domainVote.getPollOptionId(),
                domainVote.getPollId(),
                domainVote.getCreatedAt(),
                domainVote.getStatus());
    }

    public Vote toDomain(VoteEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Vote(
                entity.getId(),
                entity.getUserId(),
                entity.getPollOptionId(),
                entity.getPollId(),
                entity.getCreatedAt(),
                entity.getStatus());
    }
}
