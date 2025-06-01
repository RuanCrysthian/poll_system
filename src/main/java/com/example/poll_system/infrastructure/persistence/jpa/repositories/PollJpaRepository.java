package com.example.poll_system.infrastructure.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.poll_system.domain.enums.PollStatus;
import com.example.poll_system.infrastructure.persistence.jpa.entities.PollEntity;

@Repository
public interface PollJpaRepository extends JpaRepository<PollEntity, String> {

    @Query("SELECT p FROM PollEntity p LEFT JOIN FETCH p.options WHERE p.status = :status")
    List<PollEntity> findByStatus(@Param("status") PollStatus status);
}
