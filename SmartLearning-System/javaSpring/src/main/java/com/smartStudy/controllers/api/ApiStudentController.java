package com.smartStudy.controllers.api;

import com.smartStudy.pojo.Student;
import com.smartStudy.dto.SubjectDTO;
import com.smartStudy.dto.StudentDTO;
import com.smartStudy.dto.UserDTO;
import com.smartStudy.pojo.Subject;
import com.smartStudy.pojo.User;
import com.smartStudy.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api")
@CrossOrigin
public class ApiStudentController {
    @Autowired
    private StudentService studentService;

    @GetMapping("/statistic/students")
    public ResponseEntity<?> getStudentStatistics() {
        Long total = studentService.quantityAllStudent();
        Long week = studentService.quantityStudentWeek();
        Long month = studentService.quantityStudentMonth();
        Map<String, Long> result = new HashMap<>();
        result.put("total", total);
        result.put("week", week);
        result.put("month", month);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/students")
    public ResponseEntity<List<StudentDTO>> studentList(@RequestParam Map<String, String> params) {
        List<Student> students = studentService.getStudents(params);
        List<StudentDTO> dtos = students.stream().map(student -> {
            // Map user
            User user = student.getUser();
            UserDTO userDto = new UserDTO(user.getEmail(),user.getName(), user.getAvatar());
            // Map subjects sang SubjectDTO
            List<SubjectDTO> subjectDTOs = student.getSubjectList().stream()
                    .map(s -> {
                        // Tạo teacherNames giống logic trước
                        String teacherNames = s.getTeacherList() != null && !s.getTeacherList().isEmpty()
                                ? s.getTeacherList().stream()
                                .map(teacher -> teacher.getUser() != null ? teacher.getUser().getName() : "Unknown")
                                .collect(Collectors.joining(", "))
                                : "No teachers assigned";
                        // Sử dụng biến teacherNames vừa tạo!
                        return new SubjectDTO(s.getId(), s.getTitle(), s.getImage(), teacherNames);
                    })
                    .collect(Collectors.toList());

            return new StudentDTO(student.getUserId(), userDto, subjectDTOs);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<StudentDTO> getStudentId(@PathVariable("id") int id) {
        Student student = studentService.getStudentById(id);
        if (student == null) return ResponseEntity.notFound().build();

        // Map user
        User user = student.getUser();
        UserDTO userDto = new UserDTO(user.getEmail(),user.getName(), user.getAvatar());

        // Map subjects sang SubjectDTO và set teacherNames
        List<SubjectDTO> subjectDTOs = student.getSubjectList().stream()
                .map(s -> {
                    String teacherNames = s.getTeacherList() != null && !s.getTeacherList().isEmpty()
                            ? s.getTeacherList().stream()
                            .map(teacher -> teacher.getUser() != null ? teacher.getUser().getName() : "Unknown")
                            .collect(Collectors.joining(", "))
                            : "No teachers assigned";
                    return new SubjectDTO(s.getId(), s.getTitle(), s.getImage(), teacherNames);
                })
                .collect(Collectors.toList());

        StudentDTO dto = new StudentDTO(student.getUserId(), userDto, subjectDTOs);
        return ResponseEntity.ok(dto);
    }
}
