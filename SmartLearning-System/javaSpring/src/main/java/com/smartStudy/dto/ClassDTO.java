package com.smartStudy.dto;
import java.util.Date;


public class ClassDTO {
    private Integer id;
    private String className;
    private Date createdAt;
    private Date updatedAt;
    private Integer totalStudent;
    private Integer totalTeacher;

    public ClassDTO(Integer id, String className, Integer totalStudent, Integer totalTeacher, Date createdAt, Date updatedAt)
    {
        this.setId(id);
     this.setClassName(className);
     this.totalTeacher = totalTeacher;
     this.totalStudent = totalStudent;
     this.createdAt = createdAt;
     this.updatedAt = updatedAt;
    }
    public Integer getTotalStudent() {
        return totalStudent;
    }

    public void setTotalStudent(Integer totalStudent) {
        this.totalStudent = totalStudent;
    }

    public Integer getTotalTeacher() {
        return totalTeacher;
    }

    public void setTotalTeacher(Integer totalTeacher) {
        this.totalTeacher = totalTeacher;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
