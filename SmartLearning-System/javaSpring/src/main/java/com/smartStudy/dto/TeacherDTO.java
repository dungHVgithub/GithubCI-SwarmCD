package com.smartStudy.dto;

import java.util.List;

public class TeacherDTO {
    private Integer userId;
    private  UserDTO user;
    private List<SubjectDTO> subjectList;

    public TeacherDTO(Integer userId, UserDTO user, List<SubjectDTO> subjectList)
    {
        this.userId = userId;
        this.user = user;
        this.subjectList = subjectList;
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
