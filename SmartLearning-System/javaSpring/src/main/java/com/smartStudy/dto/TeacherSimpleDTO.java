package com.smartStudy.dto;

import java.util.List;

public class TeacherSimpleDTO {
    private Integer userId;
    private UserSimpleDTO user;        // chứa userId, name, email
    private List<SubjectDTO> subjects; // danh sách môn mà giáo viên dạy

    public TeacherSimpleDTO() {
    }

    public TeacherSimpleDTO(Integer userId, UserSimpleDTO user, List<SubjectDTO> subjects) {
        this.userId = userId;
        this.user = user;
        this.subjects = subjects;
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public UserSimpleDTO getUser() { return user; }
    public void setUser(UserSimpleDTO user) { this.user = user; }

    public List<SubjectDTO> getSubjects() { return subjects; }
    public void setSubjects(List<SubjectDTO> subjects) { this.subjects = subjects; }
}
