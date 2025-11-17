package com.smartStudy.services;

import com.smartStudy.pojo.Chapter;

import java.util.List;
import java.util.Map;

public interface ChapterService {
    List<Chapter> getChapters(Map<String, String> params);
    long countChapters(Map<String, String> params);
    Chapter get(Integer id);
    Chapter create(Chapter c, Integer subjectId);
    Chapter update(Integer id, Chapter c, Integer subjectId);
    void delete(Integer id);
    List<Chapter> chaptersBySubjectId (int subjectId);
}
