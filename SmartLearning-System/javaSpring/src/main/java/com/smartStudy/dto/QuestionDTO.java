package com.smartStudy.dto;

public class QuestionDTO {
    private Integer id;
    private Integer excerciseId;
    private Integer orderIndex;
    private String question;
    private String solution;

    public QuestionDTO(Integer id, Integer orderIndex, String question, String solution, Integer excerciseId)
    {
        this.id = id;
        this.orderIndex = orderIndex;
        this.question = question;
        this.solution = solution;
        this.excerciseId = excerciseId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getExcerciseId() {
        return excerciseId;
    }

    public void setExcerciseId(Integer excerciseId) {
        this.excerciseId = excerciseId;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }
}
