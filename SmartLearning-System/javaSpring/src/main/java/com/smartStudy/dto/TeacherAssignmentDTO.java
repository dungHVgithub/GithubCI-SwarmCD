package com.smartStudy.dto;
import com.smartStudy.pojo.TeacherAssignment;

import java.util.List;

public class TeacherAssignmentDTO {
    private int id;
    private String className;

    private List<TeacherSimpleDTO> teachers;

    public  TeacherAssignmentDTO()
    {

    }

    public TeacherAssignmentDTO(int id, String className, List<TeacherSimpleDTO> teachers)
    {
        this.id = id;
        this.className = className;
        this.teachers = teachers;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
    public List<TeacherSimpleDTO> getTeachers() { return teachers; }
    public void setTeachers(List<TeacherSimpleDTO> teachers) { this.teachers = teachers; }

}
