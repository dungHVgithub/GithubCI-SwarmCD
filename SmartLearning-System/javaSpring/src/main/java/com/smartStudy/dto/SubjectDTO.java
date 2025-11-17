package com.smartStudy.dto;

public class SubjectDTO {
    private Integer id;
    private String title;
    private String image;
    private String teacherNames;

    public SubjectDTO()
    {

    }
    public  SubjectDTO(Integer id, String title)
    {
        this.id = id;
        this.title = title;

    }
    public SubjectDTO(Integer id, String title, String image, String teacherNames)
    {
        this.id = id;
        this.title = title;
        this.image = image;
        this.teacherNames = teacherNames;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTeacherNames() {
        return teacherNames;
    }

    public void setTeacherNames(String teacherNames) {
        this.teacherNames = teacherNames;
    }
}
