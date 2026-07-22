package com.example.tcmapp.repository;

import com.example.tcmapp.entity.QuizRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizRecordRepository extends JpaRepository<QuizRecord, Long> {

    List<QuizRecord> findTop5ByUsernameOrderByCreatedAtDesc(String username);

    @Query("select coalesce(sum(record.score), 0) from QuizRecord record where record.username = :username")
    Long sumScoreByUsername(@Param("username") String username);
}
