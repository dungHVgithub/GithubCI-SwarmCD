package com.smartStudy.controllers.api;

import com.smartStudy.dto.McqResponseDTO;
import com.smartStudy.pojo.McqResponse;
import com.smartStudy.services.McqResponseService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class ApiMcqResponseController {

    @Autowired
    private McqResponseService mcqResponseService;

    @GetMapping("/mcq-responses/submission/{submissionId}")
    public ResponseEntity<?> listBySubmission(@PathVariable(value = "submissionId") Integer submissionId) {
        if (submissionId == null) {
            return ResponseEntity.badRequest().body("submissionId is required");
        }
        List<McqResponse> data = mcqResponseService.findBySubmission(submissionId);
        // map entity -> DTO
        List<McqResponseDTO> dtos = data.stream()
                .map(McqResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/mcq-responses/{submissionId}/{questionId}")
    public ResponseEntity<?> detail(@PathVariable(value = "submissionId") Integer submissionId,
                                    @PathVariable(value = "questionId") Integer questionId) {
        McqResponse r = mcqResponseService.findOne(submissionId, questionId);
        if (r == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(McqResponseDTO.fromEntity(r));
    }


    // Upsert (autosave) một lựa chọn MCQ
    // PUT /javaSpring/api/mcq-responses/{submissionId}/{questionId}?answerId=...
    @PutMapping("/mcq-responses/{submissionId}/{questionId}")
    public ResponseEntity<?> upsert(@PathVariable(value = "submissionId") Integer submissionId,
                                    @PathVariable(value = "questionId") Integer questionId,
                                    @RequestParam Integer answerId) {
        if (answerId == null) {
            return ResponseEntity.badRequest().body("answerId is required");
        }
        try {
            McqResponse saved = mcqResponseService.upsert(submissionId, questionId, answerId);
            return ResponseEntity.ok(McqResponseDTO.fromEntity(saved));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // =========================
    // Xoá 1 MCQ response của 1 câu trong submission
    // DELETE /javaSpring/api/mcq-responses/{submissionId}/{questionId}
    // =========================
    @DeleteMapping("/mcq-responses/{submissionId}/{questionId}")
    public ResponseEntity<?> deleteOne(@PathVariable(value = "submissionId") Integer submissionId,
                                       @PathVariable(value = "questionId") Integer questionId) {
        McqResponse r = mcqResponseService.findOne(submissionId, questionId);
        if (r == null) return ResponseEntity.notFound().build();
        mcqResponseService.deleteOne(submissionId, questionId);
        return ResponseEntity.ok().build();
    }
    // =========================
    // Xoá tất cả MCQ responses của một submission
    // DELETE /javaSpring/api/mcq-responses/submission/{submissionId}
    // =========================
    @DeleteMapping("/mcq-responses/submission/{submissionId}")
    public ResponseEntity<?> deleteBySubmission(@PathVariable(value = "submissionId") Integer submissionId) {
        mcqResponseService.deleteBySubmission(submissionId);
        return ResponseEntity.ok().build();
    }
}
