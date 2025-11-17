package com.smartStudy.services;

import com.smartStudy.pojo.ExerciseAnswer;

import java.util.List;
import java.util.Map;

public interface AnswerService {
    List<ExerciseAnswer> getAnswers(Map<String, String> params);
    long countAnswers(Map<String, String> params);
    ExerciseAnswer get(Integer id);
    ExerciseAnswer create(ExerciseAnswer a, Integer questionId);
    ExerciseAnswer update(Integer id, ExerciseAnswer a, Integer questionId);

    /** Trả về các cặp (questionId, correctAnswerId) cho 1 exercise */
    List<Object[]> findCorrectPairsByExercise(Integer exerciseId);
    void delete(Integer id);
}
