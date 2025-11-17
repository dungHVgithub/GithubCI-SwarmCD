package com.smartStudy.services;

import com.smartStudy.pojo.Teacher;

import java.util.List;
import java.util.Map;

public interface TeacherService {
    Long quantityAllTeacher();
    Long quantityTeacherWeek();
    Long quantityTeacherMonth();
    List<Teacher> getTeachers(Map<String,String> params);
    Teacher getTeacherById(int id);
    Teacher findByUserId(Integer userId);
}
