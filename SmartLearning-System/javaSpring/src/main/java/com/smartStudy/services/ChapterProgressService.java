package com.smartStudy.services;

import com.smartStudy.pojo.Chapter;
import com.smartStudy.pojo.ChapterProgress;
import com.smartStudy.pojo.Student;

import java.util.List;

public interface ChapterProgressService {
    List <ChapterProgress> getProgressByStudent(Integer studentId);
    ChapterProgress getProgressByStudentAndChapter(Integer studentId, Integer chapterId);
    void initializeChapterProgressForStudent(Integer student);
    ChapterProgress recalculateProgress(Integer studentId, Integer chapterId);
    ChapterProgress createProgress(ChapterProgress progress);
    ChapterProgress updateProgress(Integer studentId, Integer chapterId, ChapterProgress progress);
    ChapterProgress partialUpdateProgress(Integer studentId, Integer chapterId, ChapterProgress partialProgress);
}
