package com.smartStudy.services;

import com.smartStudy.pojo.Exercise;
import com.smartStudy.pojo.ExerciseQuestion;

import java.util.List;
import java.util.Map;

public interface QuestionService {
    List<ExerciseQuestion> getQuestions(Map<String, String> params);
    long countQuestions(Map<String, String> params);
    ExerciseQuestion get(Integer id);
    List <ExerciseQuestion> findByExcerciseId(Integer qid);
    ExerciseQuestion create(ExerciseQuestion q, Integer exerciseId);
    ExerciseQuestion update(Integer id, ExerciseQuestion q, Integer exerciseId);
    void delete(Integer id);
}
