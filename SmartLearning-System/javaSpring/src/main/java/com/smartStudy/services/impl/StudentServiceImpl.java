package com.smartStudy.services.impl;

import com.smartStudy.pojo.Student;
import com.smartStudy.repositories.StudentRepository;
import com.smartStudy.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public List<Student> getStudents(Map<String, String> params) {
        return this.studentRepository.getStudents(params);
    }

    @Override
    public Student getStudentById(int id) {
        return this.studentRepository.getStudentById(id);
    }

    @Override
    public Long quantityAllStudent() {
        return this.studentRepository.quantityAllStudent();
    }

    @Override
    public Long quantityStudentWeek() {
        return this.studentRepository.quantityStudentWeek();
    }

    @Override
    public Long quantityStudentMonth() {
        return this.studentRepository.quantityStudentMonth();
    }

    @Override
    public Student findByUserId(Integer userId) {
        return this.studentRepository.findByUserId(userId);
    }
}
