package com.example.tcmapp.controller;

import com.example.tcmapp.entity.QuizRecord;
import com.example.tcmapp.service.QuizRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/quiz")
public class QuizRecordController {

    @Autowired
    private QuizRecordService quizRecordService;

    @PostMapping("/save")
    public Map<String, Object> saveQuizRecord(@RequestBody QuizRecord quizRecord) {
        QuizRecord savedRecord = quizRecordService.saveRecord(quizRecord);
        Map<String, Object> result = progressMap(savedRecord.getUsername());
        result.put("message", "Quiz record saved successfully");
        result.put("accuracy", savedRecord.getAccuracy());
        return result;
    }

    @GetMapping("/history")
    public List<QuizRecord> getQuizHistory(@RequestParam String username) {
        List<QuizRecord> records = quizRecordService.getRecentFiveRecords(username);

        Collections.reverse(records);

        return records;
    }

    @GetMapping("/progress")
    public Map<String, Object> getLearningProgress(@RequestParam String username) {
        return progressMap(username);
    }

    private Map<String, Object> progressMap(String username) {
        QuizRecordService.LearningProgress progress = quizRecordService.getLearningProgress(username);
        Map<String, Object> result = new HashMap<>();
        result.put("rank", progress.rank());
        result.put("totalCorrect", progress.totalCorrect());
        result.put("nextThreshold", progress.nextThreshold());
        result.put("correctNeeded", progress.correctNeeded());
        return result;
    }
}
