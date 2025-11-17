/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.json.Json;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author AN515-57
 */
@Entity
@Table(name = "teacher")
@NamedQueries({
    @NamedQuery(name = "Teacher.findAll", query = "SELECT t FROM Teacher t"),
    @NamedQuery(name = "Teacher.findByUserId", query = "SELECT t FROM Teacher t WHERE t.userId = :userId")})
public class Teacher implements Serializable {

    @OneToMany(mappedBy = "createdBy")
    private List<Exercise> exerciseList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teacherId")
    @JsonIgnore
    private List<Notification> notificationList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teacher")
    @JsonIgnore
    private List<TeacherAssignment> teacherAssignmentList;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id")
    private Integer userId;
    @JoinTable(name = "teacher_subject", joinColumns = {
        @JoinColumn(name = "teacher_id", referencedColumnName = "user_id")}, inverseJoinColumns = {
        @JoinColumn(name = "subject_id", referencedColumnName = "id")})
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Subject> subjectList;

    @JoinTable(name = "teacher_class", joinColumns = {
            @JoinColumn(name = "teacher_id", referencedColumnName = "user_id")}, inverseJoinColumns = {
            @JoinColumn(name = "class_id", referencedColumnName = "id")})
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Class> classList;

    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private User user;

    public Teacher() {

    }

    public Teacher(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public List<Subject> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(List<Subject> subjectList) {
        this.subjectList = subjectList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Teacher)) {
            return false;
        }
        Teacher other = (Teacher) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.Teacher[ userId=" + userId + " ]";
    }

    public List<Class> getClassList() {
        return classList;
    }

    public void setClassList(List<Class> classList) {
        this.classList = classList;
    }

    public List<TeacherAssignment> getTeacherAssignmentList() {
        return teacherAssignmentList;
    }

    public void setTeacherAssignmentList(List<TeacherAssignment> teacherAssignmentList) {
        this.teacherAssignmentList = teacherAssignmentList;
    }

    public List<Notification> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    public List<Exercise> getExerciseList() {
        return exerciseList;
    }

    public void setExerciseList(List<Exercise> exerciseList) {
        this.exerciseList = exerciseList;
    }
}
