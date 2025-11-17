package com.smartStudy.dto;

public class ChapterDTO {
    private Integer id;
    private int orderIndex;
    private String title;

    private SubjectDTO subject;

    public ChapterDTO(Integer id, int orderIndex,String title, SubjectDTO subject)
    {
        this.id = id;
        this.orderIndex = orderIndex;
        this.title = title;
        this.subject = subject;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SubjectDTO getSubject() {
        return subject;
    }

    public void setSubject(SubjectDTO subject) {
        this.subject = subject;
    }
}
