/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author AN515-57
 */
@Embeddable
public class TeacherAssignmentPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "teacher_id")
    private int teacherId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "subject_id")
    private int subjectId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "class_id")
    private int classId;

    public TeacherAssignmentPK() {
    }

    public TeacherAssignmentPK(int teacherId, int subjectId, int classId) {
        this.teacherId = teacherId;
        this.subjectId = subjectId;
        this.classId = classId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) teacherId;
        hash += (int) subjectId;
        hash += (int) classId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TeacherAssignmentPK)) {
            return false;
        }
        TeacherAssignmentPK other = (TeacherAssignmentPK) object;
        if (this.teacherId != other.teacherId) {
            return false;
        }
        if (this.subjectId != other.subjectId) {
            return false;
        }
        if (this.classId != other.classId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.TeacherAssignmentPK[ teacherId=" + teacherId + ", subjectId=" + subjectId + ", classId=" + classId + " ]";
    }
    
}
