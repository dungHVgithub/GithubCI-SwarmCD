package com.smartStudy.repositories;

import com.smartStudy.pojo.Student;
import com.smartStudy.pojo.Teacher;

import java.util.List;
import java.util.Map;

public interface StudentRepository {
    List<Student> getStudents (Map<String,String> params);
    Student getStudentById(int id);

    long quantityAllStudent();
    long quantityStudentWeek();
    long quantityStudentMonth();
    Student findByUserId(Integer userId);
}
