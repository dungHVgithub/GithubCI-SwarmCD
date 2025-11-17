package com.smartStudy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.smartStudy.pojo.ExerciseSubmission;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubmissionDTO {
    private Integer id;
    private Integer exerciseId;      // map từ ExerciseSubmission.getExerciseId().getId()
    private Integer studentId;       // map từ ExerciseSubmission.getStudentId().getId()
    private Date submittedAt;
    private BigDecimal grade;        // entity đang dùng "grade"
    private String feedback;         // giữ để thầy/cô xem/chấm

    private String status;

    public static SubmissionDTO fromEntity(ExerciseSubmission s) {
        if (s == null) return null;
        SubmissionDTO d = new SubmissionDTO();
        d.id = s.getId();
        // ===== LƯU Ý: mapping đúng với entity hiện tại =====
        d.exerciseId = (s.getExerciseId() != null ? s.getExerciseId().getId() : null);
        d.studentId  = (s.getStudent()  != null ? s.getStudent().getUserId()  : null);
        d.submittedAt = s.getSubmittedAt();
        d.grade = s.getGrade();
        d.feedback = s.getFeedback();
        d.status = s.getStatus();
        return d;
    }

    public static List<SubmissionDTO> fromEntities(List<ExerciseSubmission> list) {
        return list == null ? List.of() : list.stream().map(SubmissionDTO::fromEntity).toList();
    }

    // ===== getters/setters =====
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getExerciseId() { return exerciseId; }
    public void setExerciseId(Integer exerciseId) { this.exerciseId = exerciseId; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public Date getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Date submittedAt) { this.submittedAt = submittedAt; }

    public BigDecimal getGrade() { return grade; }
    public void setGrade(BigDecimal grade) { this.grade = grade; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
