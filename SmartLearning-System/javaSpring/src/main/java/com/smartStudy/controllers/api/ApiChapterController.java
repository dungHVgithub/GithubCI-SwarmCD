package com.smartStudy.controllers.api;

import com.smartStudy.dto.ChapterDTO;
import com.smartStudy.dto.SubjectDTO;
import com.smartStudy.pojo.Chapter;
import com.smartStudy.pojo.Subject;
import com.smartStudy.services.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiChapterController {
    @Autowired
    private ChapterService chapterService;

    @GetMapping("/chapters")
    public ResponseEntity<?> list(@RequestParam Map<String, String> params) {
        var items = chapterService.getChapters(params);
        var total = chapterService.countChapters(params);
        var dtos = items.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("items", dtos, "total", total));
    }

    // GET /api/chapters/{id}
    @GetMapping("/chapters/{id}")
    public ResponseEntity<?> get(@PathVariable(value = "id") Integer id) {
        Chapter c = chapterService.get(id);
        if (c == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(c));
    }

    @GetMapping("/chapters/subject/{subjectId}")
    public ResponseEntity<List<Chapter>> getChaptersBySubjectId(@PathVariable(value = "subjectId") int subjectId) {
        List<Chapter> chapters = chapterService.chaptersBySubjectId(subjectId);
        return ResponseEntity.ok(chapters);
    }

    @PostMapping("/chapters")
    public ResponseEntity<?> create(@RequestBody Chapter body,
                                    @RequestParam(required = false) Integer subjectId) {
        Chapter created = chapterService.create(body, subjectId);
        if (created == null) return ResponseEntity.badRequest().body("subjectId không hợp lệ");
        return ResponseEntity.ok(toDto(created));
    }

    // PUT /api/chapters/{id}
    @PutMapping("/chapters/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @RequestBody Chapter body,
                                    @RequestParam(required = false, value = "id") Integer subjectId) {
        Chapter updated = chapterService.update(id, body, subjectId);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(updated));
    }

    // DELETE /api/chapters/{id}
    @DeleteMapping("/chapters/{id}")
    public ResponseEntity<?> delete(@PathVariable (value = "id") Integer id) {
        chapterService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // -------- helper: entity -> DTO --------
    private ChapterDTO toDto(Chapter c) {
        SubjectDTO subjectDTO = null;
        Subject s = c.getSubjectId();
        if (s != null) {
            subjectDTO = new SubjectDTO(
                    s.getId(),
                    s.getTitle(),
                    s.getImage(),
                    s.getTeacherNames()
            );
        }
        return new ChapterDTO(
                c.getId(),
                c.getOrderIndex(),
                c.getTitle(),
                subjectDTO
        );
    }


}
