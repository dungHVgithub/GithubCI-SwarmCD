package com.smartStudy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.smartStudy.pojo.McqResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class McqResponseDTO {
    private Integer submissionId;
    private Integer questionId;
    private Integer answerId;

    public static McqResponseDTO fromEntity(McqResponse r) {
        if (r == null) return null;
        McqResponseDTO d = new McqResponseDTO();
        // Lấy ID an toàn, không cần initialize proxy
        d.submissionId = (r.getExerciseSubmission() != null ? r.getExerciseSubmission().getId() : null);
        d.questionId   = (r.getExerciseQuestion()   != null ? r.getExerciseQuestion().getId()   : null);
        d.answerId     = (r.getAnswerId()     != null ? r.getAnswerId().getId(): null);
        return d;
    }

    // getters/setters
    public Integer getSubmissionId() { return submissionId; }
    public void setSubmissionId(Integer submissionId) { this.submissionId = submissionId; }
    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }
    public Integer getAnswerId() { return answerId; }
    public void setAnswerId(Integer answerId) { this.answerId = answerId; }
}
