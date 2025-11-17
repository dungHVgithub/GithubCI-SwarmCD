package com.smartStudy.controllers.api;

import com.smartStudy.dto.EssayReSimpleDTO;
import com.smartStudy.dto.EssayResponseDTO;
import com.smartStudy.pojo.EssayResponse;
import com.smartStudy.services.EssayResponseService;
import java.util.List;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class ApiEssayResponseController {

    @Autowired
    private EssayResponseService essayResponseService;
    @GetMapping("/essay-responses/submission/{submissionId}")
    public ResponseEntity<?> listBySubmission(@PathVariable(value = "submissionId") Integer submissionId) {
        if (submissionId == null) {
            return ResponseEntity.badRequest().body("submissionId is required");
        }
        List<EssayResponse> data = essayResponseService.findBySubmission(submissionId);
        List<EssayResponseDTO> dtos = data.stream()
                .map(EssayResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // GET one
    @GetMapping("/essay-responses/{submissionId}/{questionId}")
    public ResponseEntity<?> detail(@PathVariable(value = "submissionId") Integer submissionId,
                                    @PathVariable(value = "questionId") Integer questionId) {
        EssayResponse r = essayResponseService.findOne(submissionId, questionId);
        if (r == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(EssayResponseDTO.fromEntity(r));
    }

    @GetMapping("/essay-responses/exercise/{exerciseId}")
    public ResponseEntity<List<EssayReSimpleDTO>> getEssayResponsesByExercise(@PathVariable(value = "exerciseId") Integer exerciseId) {
        List<EssayReSimpleDTO> data = essayResponseService.findByExercise(exerciseId);
        return data.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(data);
    }


    // PUT upsert (autosave) - body { "answerEssay": "..." }
    public static class EssayUpsertDTO { public String answerEssay; }
    @PutMapping("/essay-responses/{submissionId}/{questionId}")
    public ResponseEntity<?> upsert(@PathVariable(value = "submissionId") Integer submissionId,
                                    @PathVariable(value = "questionId") Integer questionId,
                                    @RequestBody EssayUpsertDTO body) {
        if (body == null || body.answerEssay == null) {
            return ResponseEntity.badRequest().body("answerEssay is required");
        }
        try {
            EssayResponse saved = essayResponseService.upsert(submissionId, questionId, body.answerEssay);
            return ResponseEntity.ok(EssayResponseDTO.fromEntity(saved));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // DELETE one
    @DeleteMapping("/essay-responses/{submissionId}/{questionId}")
    public ResponseEntity<?> deleteOne(@PathVariable(value = "submissionId") Integer submissionId,
                                       @PathVariable(value = "questionId") Integer questionId) {
        EssayResponse r = essayResponseService.findOne(submissionId, questionId);
        if (r == null) return ResponseEntity.notFound().build();
        essayResponseService.deleteOne(submissionId, questionId);
        return ResponseEntity.ok().build();
    }

    // DELETE all by submission
    @DeleteMapping("/essay-responses/submission/{submissionId}")
    public ResponseEntity<?> deleteBySubmission(@PathVariable(value = "submissionId") Integer submissionId) {
        essayResponseService.deleteBySubmission(submissionId);
        return ResponseEntity.ok().build();
    }
}
