package com.smartStudy.dto;

public class QuestionUpsertDTO {
    private Integer exerciseId;   // bắt buộc
    private Integer orderIndex;   // bắt buộc
    private String question;      // bắt buộc
    private String solution;      // optional

    public Integer getExerciseId() { return exerciseId; }
    public void setExerciseId(Integer exerciseId) { this.exerciseId = exerciseId; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getSolution() { return solution; }
    public void setSolution(String solution) { this.solution = solution; }
}
