package com.smartStudy.services.impl;

import com.smartStudy.dto.*;
import com.smartStudy.pojo.Class;
import com.smartStudy.pojo.Subject;
import com.smartStudy.pojo.Teacher;
import com.smartStudy.pojo.User;
import com.smartStudy.repositories.ClassRepository;
import com.smartStudy.services.ClassService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClassServiceImpl implements ClassService {
    @Autowired
    private ClassRepository classRepository;

    @Override
    public List<Class> getCLasses(Map<String, String> params) {
        return this.classRepository.getCLasses(params);
    }


    @Override
    public Class getClassById(int id) {
        return this.classRepository.getClassById(id);
    }

    @Override
    public Class addOrUpdate(Class c) {
        Class existingClass = null;
        if (c.getId() != null) {
            existingClass = this.classRepository.getClassById(c.getId());
        }
        // Xử lý createdAt và updatedAt
        LocalDateTime now = LocalDateTime.now();
        Date currentDate = Date.from(now.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());

        if (c.getId() == null) {
            // Khi add mới: đặt cả createdAt và updatedAt giống nhau
            c.setCreatedAt(currentDate);
            c.setUpdatedAt(currentDate);
        } else {
            // Khi update: chỉ cập nhật updatedAt, giữ nguyên createdAt
            c.setUpdatedAt(currentDate);
            if (existingClass != null) {
                c.setCreatedAt(existingClass.getCreatedAt());
            }
        }
        return this.classRepository.addOrUpdate(c);
    }

    @Override
    public void deleteClass(int id) {
        this.classRepository.deleteClass(id);
    }

    @Override
    public Integer totalStudentClass(Integer classId) {
        return this.classRepository.totalStudentClass(classId);
    }

    @Override
    public Integer totalTeacherClass(Integer classId) {
        return this.classRepository.totalTeacherClass(classId);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherAssignmentDTO getTeacherAssignment(int classId) {
        Class clazz = classRepository.getClassById(classId);
        if (clazz == null) {
            throw new RuntimeException("Class not found with id=" + classId);
        }

        // Tùy repo của bạn: nếu đã có method riêng lấy teachers theo class, dùng nó.
        // Nếu chưa có, dùng quan hệ entity + initialize trong transaction.
        List<Teacher> teachers;
        try {
            // Nếu repo có:
            teachers = classRepository.getTeachersOfClass(classId);
        } catch (Throwable ignore) {
            // Fallback: lấy từ entity, ép nạp LAZY
            Hibernate.initialize(clazz.getTeacherList());
            teachers = clazz.getTeacherList() == null ? List.of() : clazz.getTeacherList();
        }

        List<TeacherSimpleDTO> teacherDTOs = new ArrayList<>();
        for (Teacher t : teachers) {
            // Nạp danh sách môn dạy của giáo viên (tối ưu: dùng HQL riêng nếu có)
            List<Subject> subjectEntities;
            try {
                subjectEntities = classRepository.getSubjectsOfTeacher(t.getUserId());
            } catch (Throwable ignore) {
                Hibernate.initialize(t.getSubjectList());
                subjectEntities = t.getSubjectList() == null ? List.of() : t.getSubjectList();
            }

            // Map Subject -> SubjectDTO
            List<SubjectDTO> subjectDTOs = new ArrayList<>();
            for (Subject s : subjectEntities) {
                subjectDTOs.add(toSubjectDTO(s));
            }

            // Map User -> UserSimpleDTO
            UserSimpleDTO userSimple = toUserSimpleDTO(t.getUser());

            // Build TeacherSimpleDTO
            TeacherSimpleDTO ts = new TeacherSimpleDTO();
            ts.setUserId(t.getUserId());
            ts.setUser(userSimple);
            ts.setSubjects(subjectDTOs);

            teacherDTOs.add(ts);
        }

        // Build TeacherAssignmentDTO
        TeacherAssignmentDTO dto = new TeacherAssignmentDTO();
        dto.setId(clazz.getId());
        dto.setClassName(clazz.getClassName());
        dto.setTeachers(teacherDTOs);
        return dto;
    }

    @Override
    @Transactional
    public Teacher getTeacherById(int teacherId) {
        Teacher t = this.classRepository.getTeacherById(teacherId);
        if (t == null) {
            throw new RuntimeException("Teacher not found with id=" + teacherId);
        }

        // Ép nạp các quan hệ LAZY cần dùng ở Controller
        Hibernate.initialize(t.getUser());         // để đọc user.id, name, email
        Hibernate.initialize(t.getSubjectList());  // để map ra SubjectDTO list
        Hibernate.initialize(t.getClassList());
        return t;
    }

    /* ===================== Helpers mapping ===================== */

    private UserSimpleDTO toUserSimpleDTO(User u) {
        if (u == null) return null;
        UserSimpleDTO dto = new UserSimpleDTO();
        // Tùy model của bạn: fullName/name
        dto.setName(u.getName() != null ? u.getName() : u.getName());
        return dto;
    }

    private SubjectDTO toSubjectDTO(Subject s) {
        if (s == null) return null;
        SubjectDTO dto = new SubjectDTO();
        dto.setId(s.getId());
        // Tùy entity/DTO của bạn dùng title hay name:
        try {
            dto.setTitle(s.getTitle());
        } catch (Throwable ignore) {
            // nếu DTO không có title hoặc entity không có getTitle
            try {
                dto.setTitle(s.getTitle());
            } catch (Throwable ignored) {
            }
        }
        dto.setImage(s.getImage());
        dto.setTeacherNames(s.getTeacherNames());
        return dto;
    }

}
