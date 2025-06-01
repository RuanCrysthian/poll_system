package com.example.poll_system.infrastructure.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.poll_system.infrastructure.persistence.jpa.entities.PollOptionEntity;

@Repository
public interface PollOptionJpaRepository extends JpaRepository<PollOptionEntity, String> {
    List<PollOptionEntity> findByPollId(String pollId);
}
