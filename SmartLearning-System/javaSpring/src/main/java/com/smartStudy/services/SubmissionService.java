package com.smartStudy.services;

import com.smartStudy.pojo.ExerciseSubmission;

import java.util.List;
import java.util.Map;

public interface SubmissionService {
    List<ExerciseSubmission> getExerciseSubmission(Map<String, String> params);
    ExerciseSubmission findById(Integer id);

    List<ExerciseSubmission> findByExercise(Integer exerciseId, Integer studentId, String status);
    ExerciseSubmission create(ExerciseSubmission es, Integer exerciseId, Integer studentId);
    ExerciseSubmission update(Integer id, ExerciseSubmission es, Integer exerciseId, Integer studentId);
    int autoGradeMcq(Integer submissionId);
    void deleteById(Integer id);
}
