package com.smartStudy.repositories;



import com.smartStudy.pojo.Teacher;

import java.util.List;
import java.util.Map;

public interface TeacherRepository  {
    Teacher getTeacherById(int id);
    Long quantityAllTeacher();
    Long quantityTeacherWeek();
    Long quantityTeacherMonth();
    List<Teacher> getTeachers(Map<String,String> params);
    Teacher findByUserId(Integer userId);
}
