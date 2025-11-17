package com.smartStudy.repositories;

import com.smartStudy.pojo.Class;
import com.smartStudy.pojo.Subject;
import com.smartStudy.pojo.Teacher;

import java.util.List;
import java.util.Map;

public interface ClassRepository {
    List<Class> getCLasses (Map<String,String> params);
    List <Teacher> getTeachersOfClass(int classId);
    List <Subject> getSubjectsOfTeacher(int teacherId);
    Teacher getTeacherById(int teacherId);
    Class getClassById(int id);
    Class addOrUpdate(Class c);
    void deleteClass(int id);
    Integer totalStudentClass(Integer classId);
    Integer totalTeacherClass(Integer classId);
}
