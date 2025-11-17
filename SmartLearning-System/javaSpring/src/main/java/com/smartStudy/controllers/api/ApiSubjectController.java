package com.smartStudy.controllers.api;


import com.smartStudy.pojo.Subject;
import com.smartStudy.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiSubjectController {
    @Autowired
    private SubjectService subjectService;

    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> subjectList(@RequestParam Map<String, String> params)
    {
        List<Subject> subjects = subjectService.getSubjects(params);
        // Set teacherNames cho từng subject trong list
        subjects.forEach(subject -> {
            String teacherNames = subject.getTeacherList() != null && !subject.getTeacherList().isEmpty()
                    ? subject.getTeacherList().stream()
                    .map(teacher -> teacher.getUser() != null ? teacher.getUser().getName() : "Unknown")
                    .collect(Collectors.joining(", "))
                    : "No teachers assigned";
            subject.setTeacherNames(teacherNames);
        });
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("subjects/{subjectId}")
    public ResponseEntity<Subject> getSubjectById( @PathVariable (value = "subjectId") int id)
    {
        return new  ResponseEntity<> (this.subjectService.getSubjectById(id),HttpStatus.OK);
    }

    @DeleteMapping("subjects/{subjectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable(value = "subjectId") int id)
    {
        this.subjectService.deleteSubject(id);
    }


}
