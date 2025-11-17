package com.smartStudy.dto;

public class ExcerciseDTO {
    private Integer id;
    private String title;
    private String description;
    private String type;
    private ChapterDTO chapter;

    private UserSimpleDTO createdBy;

    public ExcerciseDTO(Integer id, String title, String description, String type, ChapterDTO chapter, UserSimpleDTO u)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.chapter = chapter;
        this.createdBy = u;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ChapterDTO getChapter() {
        return chapter;
    }

    public void setChapter(ChapterDTO chapter) {
        this.chapter = chapter;
    }

    public UserSimpleDTO getcreatedBy() {
        return createdBy;
    }

    public void setcreatedBy(UserSimpleDTO createdBy) {
        this.createdBy = createdBy;
    }
}
