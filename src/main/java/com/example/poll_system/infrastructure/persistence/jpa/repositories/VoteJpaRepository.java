package com.example.poll_system.infrastructure.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.poll_system.infrastructure.persistence.jpa.entities.VoteEntity;

@Repository
public interface VoteJpaRepository extends JpaRepository<VoteEntity, String> {

    List<VoteEntity> findByPollId(String pollId);

    @Query("SELECT v.pollOptionId as optionId, COUNT(v) as count " +
            "FROM VoteEntity v " +
            "WHERE v.pollId = :pollId AND v.status = 'PROCESSED' " +
            "GROUP BY v.pollOptionId")
    List<VoteCountProjection> countVotesByPollIdGroupedByOptionId(@Param("pollId") String pollId);

    interface VoteCountProjection {
        String getOptionId();

        Long getCount();
    }
}
