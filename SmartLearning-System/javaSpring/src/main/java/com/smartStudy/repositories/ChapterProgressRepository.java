package com.smartStudy.repositories;

import com.smartStudy.pojo.Chapter;
import com.smartStudy.pojo.ChapterProgress;
import com.smartStudy.pojo.User;

import java.util.*;

public interface ChapterProgressRepository {
    List<ChapterProgress> getChapterProgressByStudent(Chapter chapter, User student);
    void initializeChapterProgressForStudent(Integer student);
    ChapterProgress save(ChapterProgress progress);


}
