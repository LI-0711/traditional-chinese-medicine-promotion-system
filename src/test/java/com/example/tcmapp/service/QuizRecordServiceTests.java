package com.example.tcmapp.service;

import com.example.tcmapp.entity.QuizRecord;
import com.example.tcmapp.repository.QuizRecordRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class QuizRecordServiceTests {

    private final QuizRecordRepository repository = mock(QuizRecordRepository.class);
    private final QuizRecordService service = new QuizRecordService(repository);

    @Test
    void calculatesAndPersistsAccuracy() {
        QuizRecord record = new QuizRecord();
        record.setUsername("portfolio-user");
        record.setScore(7);
        record.setTotalQuestions(10);
        when(repository.save(record)).thenReturn(record);

        QuizRecord saved = service.saveRecord(record);

        assertEquals(70, saved.getAccuracy());
        verify(repository).save(record);
    }

    @Test
    void rejectsImpossibleScores() {
        QuizRecord record = new QuizRecord();
        record.setUsername("portfolio-user");
        record.setScore(11);
        record.setTotalQuestions(10);

        assertThrows(IllegalArgumentException.class, () -> service.saveRecord(record));
    }

    @Test
    void calculatesSilverLearningProgress() {
        when(repository.sumScoreByUsername("portfolio-user")).thenReturn(32L);

        QuizRecordService.LearningProgress progress = service.getLearningProgress("portfolio-user");

        assertEquals("SILVER", progress.rank());
        assertEquals(18, progress.correctNeeded());
        assertEquals(50, progress.nextThreshold());
    }
}
