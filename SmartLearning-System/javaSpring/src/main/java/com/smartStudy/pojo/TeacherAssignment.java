/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.pojo;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author AN515-57
 */
@Entity
@Table(name = "teacher_assignment")
@NamedQueries({
    @NamedQuery(name = "TeacherAssignment.findAll", query = "SELECT t FROM TeacherAssignment t"),
    @NamedQuery(name = "TeacherAssignment.findByTeacherId", query = "SELECT t FROM TeacherAssignment t WHERE t.teacherAssignmentPK.teacherId = :teacherId"),
    @NamedQuery(name = "TeacherAssignment.findBySubjectId", query = "SELECT t FROM TeacherAssignment t WHERE t.teacherAssignmentPK.subjectId = :subjectId"),
    @NamedQuery(name = "TeacherAssignment.findByClassId", query = "SELECT t FROM TeacherAssignment t WHERE t.teacherAssignmentPK.classId = :classId")})
public class TeacherAssignment implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TeacherAssignmentPK teacherAssignmentPK;
    @JoinColumn(name = "class_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Class class1;
    @JoinColumn(name = "subject_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Subject subject;
    @JoinColumn(name = "teacher_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Teacher teacher;

    public TeacherAssignment() {
    }

    public TeacherAssignment(TeacherAssignmentPK teacherAssignmentPK) {
        this.teacherAssignmentPK = teacherAssignmentPK;
    }

    public TeacherAssignment(int teacherId, int subjectId, int classId) {
        this.teacherAssignmentPK = new TeacherAssignmentPK(teacherId, subjectId, classId);
    }

    public TeacherAssignmentPK getTeacherAssignmentPK() {
        return teacherAssignmentPK;
    }

    public void setTeacherAssignmentPK(TeacherAssignmentPK teacherAssignmentPK) {
        this.teacherAssignmentPK = teacherAssignmentPK;
    }

    public Class getClass1() {
        return class1;
    }

    public void setClass1(Class class1) {
        this.class1 = class1;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (teacherAssignmentPK != null ? teacherAssignmentPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TeacherAssignment)) {
            return false;
        }
        TeacherAssignment other = (TeacherAssignment) object;
        if ((this.teacherAssignmentPK == null && other.teacherAssignmentPK != null) || (this.teacherAssignmentPK != null && !this.teacherAssignmentPK.equals(other.teacherAssignmentPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.TeacherAssignment[ teacherAssignmentPK=" + teacherAssignmentPK + " ]";
    }
    
}
