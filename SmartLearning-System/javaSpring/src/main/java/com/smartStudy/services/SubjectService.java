/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.smartStudy.services;

import com.smartStudy.pojo.Student;
import com.smartStudy.statictis.SubjectStat;
import com.smartStudy.pojo.Subject;
import java.util.List;
import java.util.Map;

/**
 *
 * @author AN515-57
 */
public interface SubjectService {
    List <Subject>  getSubjects(Map<String,String> params);
    Subject getSubjectById(int id);
    Subject addOrUpdate(Subject s);
    void deleteSubject(int id);
    Long quantityAll();
    List<SubjectStat> countBySubjectInWeek();
    List<SubjectStat> countBySubjectInMonth();
}
