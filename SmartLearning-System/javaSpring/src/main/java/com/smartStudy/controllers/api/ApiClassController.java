package com.smartStudy.controllers.api;

import com.smartStudy.dto.TeacherSimpleDTO;
import com.smartStudy.dto.UserSimpleDTO;
import com.smartStudy.dto.TeacherAssignmentDTO;
import com.smartStudy.pojo.Class;
import com.smartStudy.dto.SubjectDTO;
import com.smartStudy.pojo.Teacher;
import com.smartStudy.services.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiClassController {

    @Autowired
    private ClassService classService;

    @GetMapping("/classes")
    public ResponseEntity<List<Class>> classList(@RequestParam Map<String, String> params) {
        List<Class> classes = classService.getCLasses(params);
        classes.forEach(c -> {
            String teacherNames = c.getTeacherList() != null && !c.getTeacherList().isEmpty()
                    ? c.getTeacherList().stream()
                    .map(teacher -> teacher.getUser() != null ? teacher.getUser().getName() : "Unknown")
                    .collect(Collectors.joining(", "))
                    : "No teachers assigned";
            c.setTeacherNames(teacherNames);
        });

        classes.forEach(c -> {
            String studentNames = c.getStudentList() != null && !c.getStudentList().isEmpty()
                    ? c.getStudentList().stream()
                    .map(student -> student.getUser() != null ? student.getUser().getName() : "Unknown")
                    .collect(Collectors.joining(", "))
                    : "No students study";
            c.setStudentNames(studentNames);
        });
        return ResponseEntity.ok(classes);
    }

    @GetMapping("classes/{classId}")
    public ResponseEntity<Class> getClassById(@PathVariable(value = "classId") int id) {
        return new ResponseEntity<>(this.classService.getClassById(id), HttpStatus.OK);
    }

    @GetMapping("assign/teacher/{teacherId}")
    public List<TeacherAssignmentDTO> getAssignmentsByTeacher(@PathVariable("teacherId") int teacherId) {
        Teacher t = classService.getTeacherById(teacherId);
//        if (t == null) {
//            throw new RuntimeException("Teacher not found with id=" + teacherId);
//        }

        // Map User -> UserSimpleDTO
        UserSimpleDTO userDTO = new UserSimpleDTO();
        if (t.getUser() != null) {
            userDTO.setName(t.getUser().getName() != null ? t.getUser().getName() : t.getUser().getName());
        }

        // Map Subjects (toàn bộ môn mà teacher dạy; nếu bạn có bảng teacher_assignment thì có thể lọc theo từng class)
        List<SubjectDTO> subjectDTOs = (t.getSubjectList() == null ? List.of()
                : t.getSubjectList().stream()
                .map(s -> new SubjectDTO(s.getId(), s.getTitle(), s.getImage(), s.getTeacherNames()))
                .toList());

        // Build teacher đơn giản (dùng lại cho mọi class của giáo viên)
        TeacherSimpleDTO teacherSimple = new TeacherSimpleDTO();
        teacherSimple.setUserId(t.getUserId());
        teacherSimple.setUser(userDTO);
        teacherSimple.setSubjects(subjectDTOs);

        // Lặp qua các lớp của giáo viên và đóng gói thành danh sách TeacherAssignmentDTO
        List<TeacherAssignmentDTO> results = new java.util.ArrayList<>();
        List<Class> classList = (t.getClassList() == null ? List.of() : t.getClassList());
        for (Class c : classList) {
            TeacherAssignmentDTO dto = new TeacherAssignmentDTO();
            dto.setId(c.getId());
            dto.setClassName(c.getClassName());
            dto.setTeachers(List.of(teacherSimple));
            results.add(dto);
        }

        return results;
    }

}