package com.smartStudy.services;

import com.smartStudy.pojo.Student;

import java.util.List;
import java.util.Map;

public interface StudentService {
    List<Student> getStudents (Map<String,String> params);
    Student getStudentById(int id);
    Long quantityAllStudent();
    Long quantityStudentWeek();
    Long quantityStudentMonth();
    Student findByUserId(Integer userId);
}
