package com.smartStudy.controllers.api;

import com.smartStudy.dto.UserSimpleDTO;
import com.smartStudy.pojo.*;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import com.smartStudy.dto.ChapterDTO;
import com.smartStudy.dto.ExcerciseDTO;
import com.smartStudy.dto.SubjectDTO;
import com.smartStudy.services.ExcerciseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiExcerciseController {

    @Autowired
    private ExcerciseService excerciseService;

    // Trong class ApiExcerciseController
    @Autowired
    private LocalSessionFactoryBean factory;

    private Session session() {
        return factory.getObject().getCurrentSession();
    }

    @GetMapping("/exercises")
    public ResponseEntity<?> list(@RequestParam Map<String, String> params) {
        List<Exercise> items = excerciseService.getExercises(params);
        long total = excerciseService.countExercises(params);

        List<ExcerciseDTO> dtos = items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        Map<String, Object> payload = new HashMap<>();
        payload.put("items", dtos);
        payload.put("total", total);
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/excercises/{excerciseId}")
    public ResponseEntity<?> get(@PathVariable(value = "excerciseId") Integer id) {
        Exercise ex = excerciseService.get(id);
        if (ex == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(ex));
    }

    @GetMapping("/exercises/chapter/{chapterId}")
    public ResponseEntity<?> getExcerciseByChapter(@PathVariable(value = "chapterId") Integer chapterId) {
        List<Exercise> items = excerciseService.findByChapterId(chapterId);
        List<ExcerciseDTO> dtos = items.stream().map(this::toDto).collect(Collectors.toList());
        Map<String, Object> payload = new HashMap<>();
        payload.put("items", dtos);
        payload.put("total", dtos.size());
        return ResponseEntity.ok(payload);
    }

    //=====  CREATE  =====
    @PostMapping("/exercises")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> req,
                                    @RequestParam(required = false) Integer chapterId) {
        Exercise ex = new Exercise();

        ex.setTitle((String) req.getOrDefault("title", ""));
        ex.setDescription((String) req.getOrDefault("description", ""));
        Object type = req.get("type");
        if (type != null) ex.setType(String.valueOf(type));

        // created_at (ISO 8601 từ frontend)
        Object created = req.get("created_at");
        if (created != null) {
            try { ex.setCreatedAt(Date.from(Instant.parse(String.valueOf(created)))); }
            catch (Exception ignored) { /* service sẽ fallback */ }
        }

        // teacherId -> createdBy.userId
        Integer teacherId = parseIntObj(req.get("teacherId"));
        if (teacherId != null) {
            Teacher t = new Teacher();
            t.setUserId(teacherId);
            ex.setCreatedBy(t);
        }

        Exercise createdEx = excerciseService.create(ex, chapterId);
        if (createdEx == null) return ResponseEntity.badRequest().body("chapterId không hợp lệ");
        return ResponseEntity.ok(toDto(createdEx));
    }

    @PutMapping("/exercises/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @RequestBody Map<String, Object> req,
                                    @RequestParam(required = false, value = "chapterId") Integer chapterId) { // sửa value="chapterId"
        Exercise ex = new Exercise();

        if (req.containsKey("title"))       ex.setTitle((String) req.get("title"));
        if (req.containsKey("description")) ex.setDescription((String) req.get("description"));
        if (req.containsKey("type"))        ex.setType(String.valueOf(req.get("type")));

        Object created = req.get("created_at");
        if (created != null) {
            try { ex.setCreatedAt(Date.from(Instant.parse(String.valueOf(created)))); }
            catch (Exception ignored) { /* optional */ }
        }

        Integer teacherId = parseIntObj(req.get("teacherId"));
        if (teacherId != null) {
            Teacher t = new Teacher();
            t.setUserId(teacherId);
            ex.setCreatedBy(t);
        }

        Exercise updated = excerciseService.update(id, ex, chapterId);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(updated));
    }

    // helper nhỏ trong controller
    private Integer parseIntObj(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).intValue();
        try { String s = String.valueOf(o); return s.isBlank() ? null : Integer.parseInt(s); }
        catch (NumberFormatException e) { return null; }
    }

    // ====== DELETE ======
    @DeleteMapping("/exercises/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Integer id) {
        excerciseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ----------------- helpers -----------------
    private ExcerciseDTO toDto(Exercise e) {
        ChapterDTO chapterDTO = null;
        Chapter chapter = e.getChapterId();

        if (chapter != null) {
            SubjectDTO subjectDTO = null;

            // lấy Subject từ Chapter (giữ nguyên ý tưởng reflection của bạn)
            Subject subject = null;
            try {
                subject = (Subject) chapter.getClass().getMethod("getSubjectId").invoke(chapter);
            } catch (Exception ex) {
                try {
                    subject = (Subject) chapter.getClass().getMethod("getSubject").invoke(chapter);
                } catch (Exception ignored) {
                    // no-op
                }
            }

            if (subject != null) {
                subjectDTO = new SubjectDTO(
                        subject.getId(),
                        subject.getTitle(),
                        subject.getImage(),
                        subject.getTeacherNames()
                );
            }

            chapterDTO = new ChapterDTO(
                    chapter.getId(),
                    chapter.getOrderIndex(),
                    chapter.getTitle(),
                    subjectDTO
            );
        }

        // createdBy -> UserSimpleDTO
        UserSimpleDTO createdByDTO = null;
        if (e.getCreatedBy() != null && e.getCreatedBy().getUser() != null) {
            User t = e.getCreatedBy().getUser();
            createdByDTO = new UserSimpleDTO(
                    t.getId(),      // ghi chú của bạn: khóa chính là userId/ id theo entity User
                    t.getName(),
                    t.getEmail()
            );
        }

        return new ExcerciseDTO(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getType(),
                chapterDTO,
                createdByDTO
        );
    }
}
