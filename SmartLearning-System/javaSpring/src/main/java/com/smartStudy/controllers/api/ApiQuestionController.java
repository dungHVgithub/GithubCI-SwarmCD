package com.smartStudy.controllers.api;

import com.smartStudy.dto.ExcerciseDTO;
import com.smartStudy.dto.QuestionDTO;
import com.smartStudy.dto.QuestionUpsertDTO;
import com.smartStudy.pojo.Exercise;
import com.smartStudy.pojo.ExerciseQuestion;
import com.smartStudy.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiQuestionController {

    @Autowired
    private QuestionService questionService;


    @GetMapping("/questions")
    public ResponseEntity<?> list(@RequestParam Map<String, String> params) {
        List<ExerciseQuestion> items = questionService.getQuestions(params);
        long total = questionService.countQuestions(params);

        List<QuestionDTO> dtos = items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        Map<String, Object> payload = new HashMap<>();
        payload.put("items", dtos);
        payload.put("total", total);
        return ResponseEntity.ok(payload);
    }

    // GET /api/questions/{id}
    @GetMapping("/questions/{questionId}")
    public ResponseEntity<?> get(@PathVariable(value = "questionId") Integer id) {
        ExerciseQuestion q = questionService.get(id);
        if (q == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(q));
    }

    @GetMapping("/questions/exercise/{exerciseId}")
    public ResponseEntity<?> listByExercise(
            @PathVariable(value = "exerciseId") Integer exerciseId,
            @RequestParam Map<String, String> params) {

        // ép exerciseId từ path vào params để tái dùng repository/service hiện có
        params.put("exerciseId", String.valueOf(exerciseId));

        var items = questionService.getQuestions(params);
        var total = questionService.countQuestions(params);

        var dtos = items.stream()
                .map(this::toDto) // dùng helper toDto(ExerciseQuestion) bạn đã có
                .toList();

        Map<String, Object> payload = new HashMap<>();
        payload.put("items", dtos);
        payload.put("total", total);
        payload.put("exerciseId", exerciseId);
        // (tuỳ chọn) phản chiếu page/size/sort/dir nếu FE có truyền
        if (params.containsKey("page")) payload.put("page", params.get("page"));
        if (params.containsKey("size")) payload.put("size", params.get("size"));
        if (params.containsKey("sort")) payload.put("sort", params.get("sort"));
        if (params.containsKey("dir")) payload.put("dir", params.get("dir"));

        return ResponseEntity.ok(payload);
    }


    // POST /api/questions
    // POST /api/questions?exerciseId=...
    @PostMapping("/questions")
    public ResponseEntity<?> create(@RequestBody QuestionUpsertDTO req,
                                    @RequestParam Integer exerciseId) {
        if (/* req.getExerciseId() == null || */ req.getOrderIndex() == null || req.getQuestion() == null) {
            return ResponseEntity.badRequest().body("orderIndex, question are required");
        }
        ExerciseQuestion q = new ExerciseQuestion();
        q.setOrderIndex(req.getOrderIndex());
        q.setQuestion(req.getQuestion());
        q.setSolution(req.getSolution());
        ExerciseQuestion created = questionService.create(q, exerciseId);
        if (created == null) return ResponseEntity.badRequest().body("Invalid exerciseId");
        return ResponseEntity.ok(toDto(created));
    }


    // PUT /api/questions/{id}
    @PutMapping("/questions/{questionId}")
    public ResponseEntity<?> update(@PathVariable(value = "questionId") Integer id, @RequestBody QuestionUpsertDTO req,
                                    @RequestParam Integer exerciseId) {
        ExerciseQuestion patch = new ExerciseQuestion();
        if (req.getOrderIndex() != null) patch.setOrderIndex(req.getOrderIndex());
        if (req.getQuestion() != null) patch.setQuestion(req.getQuestion());
        if (req.getSolution() != null) patch.setSolution(req.getSolution());

        ExerciseQuestion updated = questionService.update(id, patch, exerciseId);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(updated));
    }

    // DELETE /api/questions/{id}
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<?> delete(@PathVariable(value = "questionId") Integer id) {
        questionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // -------------- helper --------------
    private QuestionDTO toDto(ExerciseQuestion q) {
        Integer exerciseId = q.getExerciseId() != null ? q.getExerciseId().getId() : null;
        return new QuestionDTO(
                q.getId(),
                q.getOrderIndex(),
                q.getQuestion(),
                q.getSolution(),
                exerciseId
        );
    }
}
