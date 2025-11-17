package com.smartStudy.controllers.api;

import com.smartStudy.pojo.ChapterProgress;
import com.smartStudy.services.ChapterProgressService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ApiChapterProgressController {
    @Autowired
    private ChapterProgressService chapterProgressService;

    // GET: Lấy tất cả ChapterProgress của một Student
    @GetMapping("/chapter-progress/student/{studentId}")
    public ResponseEntity<List<ChapterProgress>> getProgressByStudent(@PathVariable(value = "studentId") Integer studentId) {
        try {
            List<ChapterProgress> progressList = chapterProgressService.getProgressByStudent(studentId);
            if (progressList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(progressList, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // GET: Lấy ChapterProgress của Student cho một Chapter cụ thể
    @GetMapping("/chapter-progress/{studentId}/{chapterId}")
    public ResponseEntity<ChapterProgress> getProgressByStudentAndChapter(
            @PathVariable(value = "studentId") Integer studentId,
            @PathVariable(value = "chapterId") Integer chapterId) {
        try {
            ChapterProgress progress = chapterProgressService.getProgressByStudentAndChapter(studentId, chapterId);
            return new ResponseEntity<>(progress, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // POST: Tạo mới ChapterProgress
    @PostMapping("/chapter-progress/initialize/{studentId}")
    public ResponseEntity<Void> initializeChapterProgress(@PathVariable(value = "studentId") Integer studentId) {
        try {
            chapterProgressService.initializeChapterProgressForStudent(studentId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // PUT: Cập nhật toàn bộ ChapterProgress
    @PutMapping("/chapter-progress/{studentId}/{chapterId}")
    public ResponseEntity<ChapterProgress> updateChapterProgress(
            @PathVariable(value = "studentId") Integer studentId,
            @PathVariable(value = "chapterId") Integer chapterId,
            @RequestBody ChapterProgress progress) {
        try {
            ChapterProgress updatedProgress = chapterProgressService.updateProgress(studentId, chapterId, progress);
            return new ResponseEntity<>(updatedProgress, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // PATCH: Cập nhật một phần ChapterProgress (ví dụ: chỉ percent hoặc lastScore)
    @PatchMapping("/chapter-progress/{studentId}/{chapterId}")
     public ResponseEntity<ChapterProgress> patchChapterProgress(
            @PathVariable(value = "studentId") Integer studentId,
            @PathVariable(value = "chapterId") Integer chapterId,
            @RequestBody ChapterProgress partialProgress) {
        try {
            ChapterProgress updatedProgress = chapterProgressService.partialUpdateProgress(studentId, chapterId, partialProgress);
            return new ResponseEntity<>(updatedProgress, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/chapter-progress/recalculate/{studentId}/{chapterId}")
    public ResponseEntity<ChapterProgress> recalculateChapterProgress(
            @PathVariable(value = "studentId") Integer studentId,
            @PathVariable(value = "chapterId") Integer chapterId) {
        try {
            ChapterProgress progress = chapterProgressService.recalculateProgress(studentId, chapterId);
            return new ResponseEntity<>(progress, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}