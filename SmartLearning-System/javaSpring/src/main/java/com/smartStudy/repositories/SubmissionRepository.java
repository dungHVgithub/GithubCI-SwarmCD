package com.smartStudy.repositories;

import com.smartStudy.pojo.ExerciseSubmission;

import java.util.List;
import java.util.Map;

public interface SubmissionRepository {
    List<ExerciseSubmission> getExerciseSubmission(Map<String, String> params);
    ExerciseSubmission findById(Integer id);
    List<ExerciseSubmission> findByExercise(Integer exerciseId, Integer studentId, String status);

    ExerciseSubmission save(ExerciseSubmission es);
    void deleteById(Integer id);
}
