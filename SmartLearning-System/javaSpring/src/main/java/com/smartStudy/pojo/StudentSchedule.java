package com.smartStudy.pojo;

import com.smartStudy.dto.StudentDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "student_schedule")
public class StudentSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false) @NotNull
    @Column(name = "study_date", nullable = false)
    private LocalDate studyDate;             // đổi từ java.util.Date -> LocalDate

    @Basic(optional = false) @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;             // đổi từ java.util.Date -> LocalTime

    @Basic(optional = false) @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;               // đổi từ java.util.Date -> LocalTime

    @Size(max = 255)
    @Column(name = "note")
    private String note;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;         // đổi sang LocalDateTime

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;         // đổi sang LocalDateTime

    @JoinColumn(name = "student_id", referencedColumnName = "user_id", nullable = false)
    @ManyToOne(optional = false)
    private Student studentId;

    @JoinColumn(name = "subject_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Subject subjectId;

    /* --- Lifecycle: tự set createdAt/updatedAt --- */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /* --- Getters/Setters --- */
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getStudyDate() { return studyDate; }
    public void setStudyDate(LocalDate studyDate) { this.studyDate = studyDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Student getStudentId() { return studentId; }
    public void setStudentId(Student studentId) { this.studentId = studentId; }

    public Subject getSubjectId() { return subjectId; }
    public void setSubjectId(Subject subjectId) { this.subjectId = subjectId; }

    /* equals/hashCode/toString: giữ nguyên như cũ theo id */
}
