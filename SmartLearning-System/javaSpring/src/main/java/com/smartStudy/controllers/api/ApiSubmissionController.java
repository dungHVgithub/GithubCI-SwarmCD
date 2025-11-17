package com.smartStudy.controllers.api;


import com.smartStudy.dto.SubmissionDTO;
import com.smartStudy.pojo.ExerciseSubmission;
import com.smartStudy.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiSubmissionController {
    @Autowired
    private SubmissionService submissionService;

    @GetMapping("/submissions")
    public ResponseEntity<?> list(@RequestParam Map<String, String> params) {
        var data = submissionService.getExerciseSubmission(params);
        return ResponseEntity.ok(SubmissionDTO.fromEntities(data));
    }

    @GetMapping("/submissions/{id}")
    public ResponseEntity<?> detail(@PathVariable(value = "id") Integer id) {
        var s = submissionService.findById(id);
        if (s == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(SubmissionDTO.fromEntity(s));
    }
    @GetMapping("/submissions/exercise/{exerciseId}")
    public ResponseEntity<?> listByExercise(
            @PathVariable(value = "exerciseId") Integer exerciseId,
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) String status) {
        if (exerciseId == null) {
            return ResponseEntity.badRequest().body("exerciseId is required");
        }
        var list = submissionService.findByExercise(exerciseId, studentId, status);
        return ResponseEntity.ok(SubmissionDTO.fromEntities(list));
    }


    //Create lưu bài lần đầu
    @PostMapping("/submissions")
    public ResponseEntity<?> create(@RequestBody ExerciseSubmission req,
                                    @RequestParam Integer exerciseId,
                                    @RequestParam Integer studentId) {
        var created = submissionService.create(req, exerciseId, studentId);
        if (created == null) return ResponseEntity.badRequest().body("Create submission failed");
        return ResponseEntity.ok(SubmissionDTO.fromEntity(created));
    }

    @PutMapping("/submissions/{id}")
    public ResponseEntity<?> update(@PathVariable(value = "id") Integer id,
                                    @RequestBody ExerciseSubmission req,
                                    @RequestParam(required = false) Integer exerciseId,
                                    @RequestParam(required = false) Integer studentId) {
        var updated = submissionService.update(id, req, exerciseId, studentId);
        if (updated == null) return ResponseEntity.badRequest().body("Update submission failed");
        return ResponseEntity.ok(SubmissionDTO.fromEntity(updated));
    }

    @PostMapping("/submissions/{id}/regrade-mcq")
    public ResponseEntity<?> regradeMcq(@PathVariable(value = "id") Integer id) {
        try {
            int mcqScore = submissionService.autoGradeMcq(id);
            var s = submissionService.findById(id);
            Map<String, Object> payload = new HashMap<>();
            payload.put("mcqScore", mcqScore);
            payload.put("submission", SubmissionDTO.fromEntity(s));
            return ResponseEntity.ok(payload);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    @DeleteMapping("/submissions/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Integer id) {
        ExerciseSubmission existed = submissionService.findById(id);
        if (existed == null)
            return ResponseEntity.notFound().build();
        submissionService.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
