package com.smartStudy.controllers.api;

import com.smartStudy.dto.StudentDTO;
import com.smartStudy.dto.SubjectDTO;
import com.smartStudy.dto.TeacherDTO;
import com.smartStudy.dto.UserDTO;
import com.smartStudy.pojo.Student;
import com.smartStudy.pojo.Teacher;
import com.smartStudy.pojo.User;
import com.smartStudy.services.TeacherService;
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
public class ApiTeacherController {
    @Autowired
    private TeacherService teacherService;

    @GetMapping("/statistic/teachers")
    public ResponseEntity<?> getTeacherStatistics() {
        Long total = teacherService.quantityAllTeacher();
        Long week = teacherService.quantityTeacherWeek();
        Long month = teacherService.quantityTeacherMonth();

        Map<String, Long> result = new HashMap<>();
        result.put("total", total);
        result.put("week", week);
        result.put("month", month);

        return ResponseEntity.ok(result);
    }
    @GetMapping("/teachers")
    public ResponseEntity<List<TeacherDTO>> teacherList(@RequestParam Map<String, String> params) {
        List<Teacher> teachers = teacherService.getTeachers(params);
        List<TeacherDTO> dtos = teachers.stream().map(teacher -> {
            // Map user
            User user = teacher.getUser();
            UserDTO userDto = new UserDTO(user.getEmail(), user.getName(),user.getAvatar());

            // Map subjectList
            List<SubjectDTO> subjectDTOs = teacher.getSubjectList().stream()
                    .map(s -> {
                        String teacherNames = s.getTeacherList() != null && !s.getTeacherList().isEmpty()
                                ? s.getTeacherList().stream()
                                .map(t -> t.getUser() != null ? t.getUser().getName() : "Unknown")
                                .collect(Collectors.joining(", "))
                                : "No teachers assigned";
                        return new SubjectDTO(s.getId(), s.getTitle(), s.getImage(), teacherNames);
                    })
                    .collect(Collectors.toList());

            return new TeacherDTO(
                    teacher.getUserId(),
                    userDto,
                    subjectDTOs
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/teachers/{id}")
    public ResponseEntity<TeacherDTO> getTeacherById(@PathVariable("id") int id) {
        Teacher teacher = teacherService.getTeacherById(id);
        if (teacher == null) return ResponseEntity.notFound().build();

        User user = teacher.getUser();
        UserDTO userDto = new UserDTO(user.getEmail(), user.getName(), user.getAvatar());

        List<SubjectDTO> subjectDTOs = teacher.getSubjectList().stream()
                .map(s -> {
                    String teacherNames = s.getTeacherList() != null && !s.getTeacherList().isEmpty()
                            ? s.getTeacherList().stream()
                            .map(t -> t.getUser() != null ? t.getUser().getName() : "Unknown")
                            .collect(Collectors.joining(", "))
                            : "No teachers assigned";
                    return new SubjectDTO(s.getId(), s.getTitle(), s.getImage(), teacherNames);
                })
                .collect(Collectors.toList());


        TeacherDTO dto = new TeacherDTO(
                teacher.getUserId(),
                userDto,
                subjectDTOs
        );
        return ResponseEntity.ok(dto);
    }
}
