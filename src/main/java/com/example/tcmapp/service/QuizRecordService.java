package com.example.tcmapp.service;

import com.example.tcmapp.entity.QuizRecord;
import com.example.tcmapp.repository.QuizRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizRecordService {

    private final QuizRecordRepository quizRecordRepository;

    public QuizRecordService(QuizRecordRepository quizRecordRepository) {
        this.quizRecordRepository = quizRecordRepository;
    }

    public QuizRecord saveRecord(QuizRecord quizRecord) {
        if (quizRecord.getUsername() == null || quizRecord.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (quizRecord.getScore() == null || quizRecord.getTotalQuestions() == null
                || quizRecord.getTotalQuestions() <= 0 || quizRecord.getTotalQuestions() > 10
                || quizRecord.getScore() < 0 || quizRecord.getScore() > quizRecord.getTotalQuestions()) {
            throw new IllegalArgumentException("Invalid quiz score");
        }
        if (quizRecord.getTopicStats() != null && quizRecord.getTopicStats().length() > 4000) {
            throw new IllegalArgumentException("Topic statistics are too large");
        }
        quizRecord.setAccuracy((int) Math.round(
                quizRecord.getScore() * 100.0 / quizRecord.getTotalQuestions()));
        return quizRecordRepository.save(quizRecord);
    }

    public List<QuizRecord> getRecentFiveRecords(String username) {
        return quizRecordRepository.findTop5ByUsernameOrderByCreatedAtDesc(username);
    }

    public LearningProgress getLearningProgress(String username) {
        long totalCorrect = quizRecordRepository.sumScoreByUsername(username);
        if (totalCorrect >= 50) {
            return new LearningProgress("GOLD", totalCorrect, null, 0);
        }
        if (totalCorrect >= 20) {
            return new LearningProgress("SILVER", totalCorrect, 50, 50 - totalCorrect);
        }
        return new LearningProgress("BRONZE", totalCorrect, 20, 20 - totalCorrect);
    }

    public record LearningProgress(
            String rank,
            long totalCorrect,
            Integer nextThreshold,
            long correctNeeded
    ) {}
}
