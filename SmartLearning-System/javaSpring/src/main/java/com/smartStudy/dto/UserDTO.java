package com.smartStudy.dto;

public class UserDTO {
    private  String mail;
    private String name;

    private String avatar;

    public UserDTO( String mail, String name, String avatar)
    {
        this.mail = mail;
        this.name = name;
        this.setAvatar(avatar);
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
