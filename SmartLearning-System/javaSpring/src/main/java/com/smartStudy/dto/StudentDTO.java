package com.smartStudy.dto;

import com.smartStudy.pojo.Subject;
import com.smartStudy.pojo.User;

import java.util.List;

public class StudentDTO {
    private Integer userId;
    private UserDTO user;
    private List<SubjectDTO> subjectList;

    public StudentDTO(Integer userId, UserDTO user, List<SubjectDTO> subjectList) {
        this.setUserId(userId);
        this.setUser(user);
        this.setSubjectList(subjectList);
    }
    public StudentDTO (Integer userId)
    {
        this.userId = userId;
    }

    public StudentDTO(Integer userId, User user) {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public List<SubjectDTO> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(List<SubjectDTO> subjectList) {
        this.subjectList = subjectList;
    }
}
