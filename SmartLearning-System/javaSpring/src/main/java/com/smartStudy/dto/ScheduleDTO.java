package com.smartStudy.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartStudy.pojo.StudentSchedule;
import  java.time.*;

public class ScheduleDTO {
    private Integer id;
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate studyDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String note;
    private LocalDateTime createdAt;
    private  LocalDateTime updatedAt;
    private StudentDTO student;
    private SubjectDTO subject;


   public  ScheduleDTO()
   {

   }
    public static ScheduleDTO fromEntity(StudentSchedule s) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(s.getId());
        dto.setStudyDate(s.getStudyDate());
        dto.setStartTime(s.getStartTime());
        dto.setEndTime(s.getEndTime());
        dto.setNote(s.getNote());
        dto.setCreatedAt(s.getCreatedAt());
        dto.setUpdatedAt(s.getUpdatedAt());
        // subject tối giản (id + title)
        if (s.getSubjectId() != null) {
            SubjectDTO subj = new SubjectDTO(
                    s.getSubjectId().getId(),
                    s.getSubjectId().getTitle()
            );
            dto.setSubject(subj);
        }
        if(s.getStudentId() != null)
        {
         StudentDTO studentDTO = new StudentDTO(s.getStudentId().getUserId());
         dto.setStudent(studentDTO);
        }
        return dto;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getStudyDate() {
        return studyDate;
    }

    public void setStudyDate(LocalDate studyDate) {
        this.studyDate = studyDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public StudentDTO getStudent() {
        return student;
    }

    public void setStudent(StudentDTO student) {
        this.student = student;
    }
    public SubjectDTO getSubject() { return subject; }
    public void setSubject(SubjectDTO subject) { this.subject = subject; }
}
