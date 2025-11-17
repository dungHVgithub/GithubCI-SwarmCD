package com.smartStudy.repositories;

import com.smartStudy.pojo.ExerciseQuestion;

import java.util.List;
import java.util.Map;

public interface QuestionRepository {
    List<ExerciseQuestion> getQuestions(Map<String, String> params);
    long countQuestions(Map<String, String> params);
    ExerciseQuestion findById(Integer id);
    List <ExerciseQuestion> findByExcerciseId(Integer excerciseId);
    ExerciseQuestion save(ExerciseQuestion q);
    void deleteById(Integer id);
}
