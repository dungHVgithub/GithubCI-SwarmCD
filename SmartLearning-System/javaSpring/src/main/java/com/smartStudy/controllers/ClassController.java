package com.smartStudy.controllers;

import com.smartStudy.dto.ClassDTO;
import com.smartStudy.pojo.Class;
import com.smartStudy.pojo.Student;
import com.smartStudy.pojo.Teacher;
import com.smartStudy.services.ClassService;
import com.smartStudy.services.StudentService;
import com.smartStudy.services.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class ClassController {
    @Autowired
    private ClassService classService;
    @Autowired
    private TeacherService teacherService;

    @Autowired
    private StudentService studentService;

    @GetMapping("/classes")
    public String classView(Model model, @RequestParam(required = false) Map<String, String> params)
    {
        List <Class> classList = classService.getCLasses(params);
        List < ClassDTO> classDTOS = new ArrayList<>();
        for (Class c : classList) {
            Integer totalStudent = classService.totalStudentClass(c.getId());
            Integer totalTeacher = classService.totalTeacherClass(c.getId());
            classDTOS.add(new ClassDTO(
                    c.getId(),
                    c.getClassName(),
                    totalStudent,
                    totalTeacher,
                    c.getCreatedAt(),
                    c.getUpdatedAt()
            ));
        }
        model.addAttribute("classes", classDTOS);
        return "classes";
    }

    @GetMapping("classes/add")
    public String editClassView(Model model)
    {
        model.addAttribute("class",new Class());
        return "editClass";
    }


    @PostMapping("/classes/add")
    public String addClass(
            @ModelAttribute("class") Class classObj,
            @RequestParam(value = "studentIds", required = false) List<Integer> studentIds,
            @RequestParam(value = "teacherIds", required = false) List<Integer> teacherIds,
            Model model
    ) {
        List<Student> students = new ArrayList<>();
        if (studentIds != null) {
            for (Integer id : studentIds) {
                Student s = studentService.findByUserId(id);
                if (s != null) students.add(s);
            }
        }
        List<Teacher> teachers = new ArrayList<>();
        if (teacherIds != null) {
            for (Integer id : teacherIds) {
                Teacher t = teacherService.findByUserId(id);
                if (t != null) teachers.add(t);
            }
        }
        classObj.setStudentList(students);
        classObj.setTeacherList(teachers);

        classService.addOrUpdate(classObj);

        System.out.println("Students sẽ gán vào class: " + students);
        System.out.println("Teachers sẽ gán vào class: " + teachers);

        return "redirect:/classes";
    }
    @GetMapping("/classes/{classId}")
    public String updateSubjectView(Model model, @PathVariable(value = "classId") int id) {
        model.addAttribute("class", this.classService.getClassById(id));
        return "editClass";
    }

    @DeleteMapping("/classes/{classId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable(value = "classId") int id) {
        this.classService.deleteClass(id);
    }
}
