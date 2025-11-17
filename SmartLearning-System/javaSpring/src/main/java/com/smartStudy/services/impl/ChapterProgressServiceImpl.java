package com.smartStudy.services.impl;

import com.smartStudy.pojo.*;
import com.smartStudy.repositories.*;
import com.smartStudy.services.ChapterProgressService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.*;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ChapterProgressServiceImpl implements ChapterProgressService {
    @Autowired
    private ChapterProgressRepository chapterProgressRepo;
    @Autowired
    private ChapterRepository chapterRepo;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Override
    public List<ChapterProgress> getProgressByStudent(Integer studentId) {
        User student = userRepository.getUserById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("StudentId must not be null");
        }
        return chapterProgressRepo.getChapterProgressByStudent(null, student);
    }

    @Override
    public ChapterProgress getProgressByStudentAndChapter(Integer studentId, Integer chapterId) {
        User student = userRepository.getUserById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("StudentId must not be null");
        }
        Chapter chapter = chapterRepo.findById(chapterId);
        if (chapter == null) {
            throw new IllegalArgumentException("ChapterId must not be null");
        }
        List<ChapterProgress> progressList = chapterProgressRepo.getChapterProgressByStudent(chapter, student);
        if (progressList.isEmpty()) {
            throw new EntityNotFoundException("ChapterProgress not found for student " + studentId + " and chapter " + chapterId);
        }
        return progressList.get(0);
    }

    @Override
    public void initializeChapterProgressForStudent(Integer studentId) {
        User student = userRepository.getUserById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("StudentId must not be null");
        }
        chapterProgressRepo.initializeChapterProgressForStudent(studentId);
    }

    @Override
    public ChapterProgress recalculateProgress(Integer studentId, Integer chapterId) {
        User student = userRepository.getUserById(studentId);
        Chapter chapter = chapterRepo.findById(chapterId);
        List<ChapterProgress> progressList = chapterProgressRepo.getChapterProgressByStudent(chapter, student);
        if (student == null) {
            throw new IllegalArgumentException("StudentId must not be null");
        }
        if (chapter == null) {
            throw new IllegalArgumentException("ChapterId must not be null");
        }
        if (progressList.isEmpty()) {
            throw new EntityNotFoundException("ChapterProgress not found for student " + studentId + " and chapter " + chapterId);
        }
        ChapterProgress progress = progressList.isEmpty() ? new ChapterProgress() : progressList.get(0);

        // Lấy danh sách Exercise trong Chapter
        List<Exercise> exercises = exerciseRepository.findByChapterId(chapter.getId());
        int totalExercises = exercises.size();
        if (totalExercises == 0) {
            progress.setPercent(0);
            progress.setLastScore(BigDecimal.ZERO);
            progress.setUpdatedAt(new Date());
            return chapterProgressRepo.save(progress);
        }

        // Khởi tạo biến để tính toán
        BigDecimal totalGrade = BigDecimal.ZERO;
        int completedCount = 0;

        for (Exercise exercise : exercises) {
            // Lấy danh sách submission cho Exercise (đã sorted DESC theo submittedAt)
            List<ExerciseSubmission> submissions = submissionRepository.findByExercise(exercise.getId(), studentId, null);
            if (!submissions.isEmpty()) {
                ExerciseSubmission latestSubmission = submissions.get(0);  // Phần tử đầu là mới nhất
                BigDecimal grade = latestSubmission.getGrade();
                if (grade != null && grade.compareTo(BigDecimal.ZERO) > 0) {
                    totalGrade = totalGrade.add(grade);
                    completedCount++;
                }
            }
        }

        // Tính percent
        int percent = (completedCount * 100) / totalExercises;
// Tính lastScore (trung bình grade, làm tròn đến 2 chữ số thập phân)
        BigDecimal lastScore = BigDecimal.ZERO;
        if (completedCount > 0) {
            BigDecimal average = totalGrade.divide(BigDecimal.valueOf(completedCount), 2, BigDecimal.ROUND_HALF_UP);
            lastScore = average.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        progress.setStudentId(student);
        progress.setChapterId(chapter);
        progress.setPercent(percent);
        progress.setLastScore(lastScore);
        progress.setUpdatedAt(new Date());

        return chapterProgressRepo.save(progress);
    }

    @Override
    public ChapterProgress createProgress(ChapterProgress progress) {
        if (progress.getStudentId() == null || progress.getChapterId() == null) {
            throw new IllegalArgumentException("StudentId and ChapterId must not be null");
        }
        progress.setUpdatedAt(new Date());
        return chapterProgressRepo.save(progress);
    }

    @Override
    public ChapterProgress updateProgress(Integer studentId, Integer chapterId, ChapterProgress progress) {
        ChapterProgress existingProgress = getProgressByStudentAndChapter(studentId, chapterId);
        existingProgress.setLastScore(progress.getLastScore());
        existingProgress.setPercent(progress.getPercent());
        existingProgress.setUpdatedAt(new Date());
        return chapterProgressRepo.save(existingProgress);
    }

    @Override
    public ChapterProgress partialUpdateProgress(Integer studentId, Integer chapterId, ChapterProgress partialProgress) {
        ChapterProgress existingProgress = getProgressByStudentAndChapter(studentId, chapterId);
        if (partialProgress.getLastScore() != null) {
            existingProgress.setLastScore(partialProgress.getLastScore());
        }
        if (partialProgress.getPercent() != null) {
            existingProgress.setPercent(partialProgress.getPercent());
        }
        existingProgress.setUpdatedAt(new Date());
        return chapterProgressRepo.save(existingProgress);
    }

}