package com.smartStudy.controllers.api;

import com.smartStudy.dto.AnswerDTO;
import com.smartStudy.dto.AnswerUpsertDTO;
import com.smartStudy.pojo.ExerciseAnswer;
import com.smartStudy.services.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiAnswerController {
    @Autowired
    private AnswerService answerService;

    @GetMapping("/answers")
    public ResponseEntity<?> list(@RequestParam Map<String, String> params) {
        var items = answerService.getAnswers(params);
        var total = answerService.countAnswers(params);
        var dtos = items.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("items", dtos, "total", total));
    }

    // GET /api/answers/{id}
    @GetMapping("/answers/{id}")
    public ResponseEntity<?> get(@PathVariable (value = "id") Integer id) {
        ExerciseAnswer a = answerService.get(id);
        if (a == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(a));
    }

    // GET /api/answers/question/{questionId}  (giống style bạn làm với questions/exercise)
    @GetMapping("/answers/question/{questionId}")
    public ResponseEntity<?> listByQuestion(@PathVariable (value = "questionId") Integer questionId,
                                            @RequestParam Map<String, String> params) {
        params.put("questionId", String.valueOf(questionId));
        var items = answerService.getAnswers(params);
        var total = answerService.countAnswers(params);
        var dtos  = items.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("items", dtos, "total", total, "questionId", questionId));
    }

    // POST /api/answers
    @PostMapping("/answers")
    public ResponseEntity<?> create(@RequestBody AnswerUpsertDTO req,
                                    @RequestParam Integer questionId) {
        if (req.getIsCorrect() == null || req.getAnswerText() == null) {
            return ResponseEntity.badRequest().body("questionId và answerText là bắt buộc");
        }
        ExerciseAnswer a = new ExerciseAnswer();
        a.setAnswerText(req.getAnswerText());
        a.setIsCorrect(req.getIsCorrect() != null ? req.getIsCorrect() : false);
        ExerciseAnswer created = answerService.create(a,questionId);
        if (created == null) return ResponseEntity.badRequest().body("questionId không hợp lệ");
        return ResponseEntity.ok(toDto(created));
    }

    // PUT /api/answers/{id}
    @PutMapping("/answers/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody AnswerUpsertDTO req) {
        ExerciseAnswer patch = new ExerciseAnswer();
        if (req.getAnswerText() != null) patch.setAnswerText(req.getAnswerText());
        if (req.getIsCorrect() != null)  patch.setIsCorrect(req.getIsCorrect());

        ExerciseAnswer updated = answerService.update(id, patch, req.getQuestionId());
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(updated));
    }

    // DELETE /api/answers/{id}
    @DeleteMapping("/answers/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        answerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- helper ----------
    private AnswerDTO toDto(ExerciseAnswer a) {
        Integer qid = a.getQuestionId() != null ? a.getQuestionId().getId() : null;
        return new AnswerDTO(a.getId(), qid, a.getAnswerText(), a.getIsCorrect());
    }
}
