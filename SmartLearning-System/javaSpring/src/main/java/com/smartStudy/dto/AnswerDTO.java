package com.smartStudy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnswerDTO {
    private Integer id;
    private Integer questionId;
    private String answerText;
    @JsonProperty("isCorrect")
    private Boolean correct;


    public AnswerDTO(Integer id, Integer questionId, String answerText, Boolean correct) {
        this.setId(id);
        this.setQuestionId(questionId);
        this.setAnswerText(answerText);
        this.setCorrect(correct);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }

    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }
}
