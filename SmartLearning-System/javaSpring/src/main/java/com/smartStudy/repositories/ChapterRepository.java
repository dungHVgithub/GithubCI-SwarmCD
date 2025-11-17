package com.smartStudy.repositories;

import com.smartStudy.pojo.Chapter;

import java.util.List;
import java.util.Map;

public interface ChapterRepository {
    List<Chapter> getChapters(Map<String, String> params);
    long countChapters(Map<String, String> params);
    Chapter findById(Integer id);
    Chapter save(Chapter c);
    void deleteById(Integer id);
    List<Chapter> chaptersBySubjectId (int subjectId);
}
