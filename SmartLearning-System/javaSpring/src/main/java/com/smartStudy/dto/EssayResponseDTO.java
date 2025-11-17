package com.smartStudy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.smartStudy.pojo.EssayResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EssayResponseDTO {
    private Integer submissionId;
    private Integer questionId;
    private String  answerEssay;

    public static EssayResponseDTO fromEntity(EssayResponse r) {
        if (r == null) return null;
        EssayResponseDTO d = new EssayResponseDTO();
        d.submissionId = (r.getExerciseSubmission() != null ? r.getExerciseSubmission().getId() : null);
        d.questionId   = (r.getExerciseQuestion()   != null ? r.getExerciseQuestion().getId()   : null);
        d.answerEssay  = r.getAnswerEssay();
        return d;
    }

    // getters/setters
    public Integer getSubmissionId() { return submissionId; }
    public void setSubmissionId(Integer submissionId) { this.submissionId = submissionId; }
    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }
    public String getAnswerEssay() { return answerEssay; }
    public void setAnswerEssay(String answerEssay) { this.answerEssay = answerEssay; }
}
