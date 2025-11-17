package com.smartStudy.dto;

public class AnswerUpsertDTO {
    private Integer questionId;  // bắt buộc khi create
    private String answerText;   // bắt buộc
    private Boolean isCorrect;   // optional

    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }

    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }

    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean correct) { isCorrect = correct; }
}
