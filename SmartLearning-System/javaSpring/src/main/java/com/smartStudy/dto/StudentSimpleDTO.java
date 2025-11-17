package com.smartStudy.dto;

public class StudentSimpleDTO {
    private int userId;
    private UserSimpleDTO user;

    public StudentSimpleDTO(int id, UserSimpleDTO u){
        this.user = u;
        this.userId = id;
    }
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public UserSimpleDTO getUser() {
        return user;
    }

    public void setUser(UserSimpleDTO user) {
        this.user = user;
    }
}
