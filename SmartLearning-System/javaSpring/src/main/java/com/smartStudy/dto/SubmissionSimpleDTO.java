package com.smartStudy.dto;

import java.math.BigDecimal;

public class SubmissionSimpleDTO {
    private int id;
    private String status;
    private String feedback;
    private BigDecimal grade;

    private StudentSimpleDTO student;

    public SubmissionSimpleDTO(int id, String status, String feedback, BigDecimal grade, StudentSimpleDTO student)
    {
        this.id = id;
        this.status = status;
        this.feedback = feedback;
        this.grade = grade;
        this.student = student;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }


    public BigDecimal getGrade() {
        return grade;
    }

    public void setGrade(BigDecimal grade) {
        this.grade = grade;
    }

    public StudentSimpleDTO getStudent() {return student;}

    public void setStudent(StudentSimpleDTO student) {
        this.student = student;
    }
}
