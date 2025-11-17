package com.smartStudy.repositories;

import com.smartStudy.pojo.ExerciseAnswer;

import java.util.List;
import java.util.Map;

public interface AnswerRepository {
    List<ExerciseAnswer> getAnswers(Map<String, String> params);
    long countAnswers(Map<String, String> params);
    ExerciseAnswer findById(Integer id);
    ExerciseAnswer save(ExerciseAnswer a);
    void deleteById(Integer id);
}
