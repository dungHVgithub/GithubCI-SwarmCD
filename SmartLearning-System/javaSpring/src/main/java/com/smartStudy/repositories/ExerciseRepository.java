package com.smartStudy.repositories;

import com.smartStudy.pojo.Exercise;
import java.util.List;
import java.util.Map;

public interface ExerciseRepository {
    List<Exercise> getExercises(Map<String, String> params);
    long countExercises(Map<String, String> params);
    Exercise findById(Integer id);
    List<Exercise> findByChapterId(Integer chapterId);

    Exercise save(Exercise ex);
    void deleteById(Integer id);
}

