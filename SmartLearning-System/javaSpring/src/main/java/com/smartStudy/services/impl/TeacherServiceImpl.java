package com.smartStudy.services.impl;

import com.smartStudy.pojo.Teacher;
import com.smartStudy.repositories.TeacherRepository;
import com.smartStudy.services.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TeacherServiceImpl implements TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;
    @Override
    public Long quantityAllTeacher() {
        return this.teacherRepository.quantityAllTeacher();
    }

    @Override
    public Long quantityTeacherWeek() {
        return this.teacherRepository.quantityTeacherWeek();
    }

    @Override
    public Long quantityTeacherMonth() {
        return this.teacherRepository.quantityTeacherMonth();
    }

    @Override
    public List<Teacher> getTeachers(Map<String, String> params) {
        return this.teacherRepository.getTeachers(params);
    }

    @Override
    public Teacher getTeacherById(int id) {
        return this.teacherRepository.getTeacherById(id);
    }

    @Override
    public Teacher findByUserId(Integer userId) {
        return this.teacherRepository.findByUserId(userId);
    }
}
