/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.controllers;

import com.smartStudy.pojo.Subject;
import com.smartStudy.statictis.SubjectStat;
import com.smartStudy.services.StudentService;
import com.smartStudy.services.TeacherService;
import com.smartStudy.services.UserService;
import org.springframework.ui.Model;
import com.smartStudy.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 *
 * @author AN515-57
 */
@Controller
@ControllerAdvice
public class IndexController {
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private StudentService studentService;

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void subjectResponse (Model model, @RequestParam(required = false) Map<String, String> params)
    {
        List<Subject> subjectList = subjectService.getSubjects(params);
        model.addAttribute("subjects", subjectList);
    }

    @ModelAttribute
    public void userRespone(Model model, @RequestParam(required = false) Map<String, String> params) {
        model.addAttribute("users", userService.getUsers(params));
    }

    @ModelAttribute
    public void teacherRespone(Model model, @RequestParam(required = false) Map<String, String> params) {
        model.addAttribute("teachers", teacherService.getTeachers(params));
    }

    @ModelAttribute
    public void studentRespone(Model model, @RequestParam(required = false) Map<String, String> params) {
        model.addAttribute("students", studentService.getStudents(params));
    }

    @ModelAttribute
    public void statictisResponse(Model model){
        Long totalTeacherWeek = teacherService.quantityTeacherWeek();
        Long totalTeacherMonth = teacherService.quantityTeacherMonth();
        Long totalStudentWeek = studentService.quantityStudentWeek();
        Long totalStudentMonth = studentService.quantityStudentMonth();
        List<SubjectStat> subjectStatsWeek = subjectService.countBySubjectInWeek();
        List<SubjectStat> subjectStatsMonth = subjectService.countBySubjectInMonth();
        model.addAttribute("subjectStatsWeek", subjectStatsWeek);
        model.addAttribute("subjectStatsMonth", subjectStatsMonth);
        model.addAttribute("totalTeacherWeek",totalTeacherWeek);
        model.addAttribute("totalTeacherMonth",totalTeacherMonth);
        model.addAttribute("totalStudentWeek",totalStudentWeek);
        model.addAttribute("totalStudentMonth",totalStudentMonth);
    }
    
    @RequestMapping("/")
        public String responseHome(Model model) {
        Long totalTeachers = teacherService.quantityAllTeacher();
        Long totalStudent = studentService.quantityAllStudent();
        Long totalSubjects = subjectService.quantityAll();
        model.addAttribute("totalTeachers",totalTeachers);
        model.addAttribute("totalStudents",totalStudent);
        model.addAttribute("totalSubjects",totalSubjects);
        return "home";
    }
}
