package com.smartStudy.services;

import com.smartStudy.pojo.Exercise;

import java.util.List;
import java.util.Map;

public interface ExcerciseService {
    List<Exercise> getExercises(Map<String, String> params);
    long countExercises(Map<String, String> params);
    Exercise get(Integer id);
    List<Exercise> findByChapterId(Integer cid);
    Exercise create(Exercise ex, Integer chapterId);
    Exercise update(Integer id, Exercise ex, Integer chapterId);
    void delete(Integer id);
}
