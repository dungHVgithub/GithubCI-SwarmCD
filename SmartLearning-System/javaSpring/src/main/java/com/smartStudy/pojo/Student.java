/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author AN515-57
 */
@Entity
@Table(name = "student")
@NamedQueries({
    @NamedQuery(name = "Student.findAll", query = "SELECT s FROM Student s"),
    @NamedQuery(name = "Student.findByUserId", query = "SELECT s FROM Student s WHERE s.userId = :userId")})
public class Student implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studentId")
    @JsonIgnore
    private List<StudentSchedule> studentScheduleList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studentId")
    @JsonIgnore
    private List<Notification> notificationList;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id")
    private Integer userId;
    @ManyToMany(mappedBy = "studentList")
    @JsonIgnore
    private List<Class> classList;
    @JoinTable(name = "student_subject", joinColumns = {
        @JoinColumn(name = "student_id", referencedColumnName = "user_id")}, inverseJoinColumns = {
        @JoinColumn(name = "subject_id", referencedColumnName = "id")})
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Subject> subjectList;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @OneToOne(optional = false)
    private User user;

    public Student() {
    }

    public Student(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<Class> getClassList() {
        return classList;
    }

    public void setClassList(List<Class> classList) {
        this.classList = classList;
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
        if (!(object instanceof Student)) {
            return false;
        }
        Student other = (Student) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.Student[ userId=" + userId + " ]";
    }

    public List<Notification> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    public List<StudentSchedule> getStudentScheduleList() {
        return studentScheduleList;
    }

    public void setStudentScheduleList(List<StudentSchedule> studentScheduleList) {
        this.studentScheduleList = studentScheduleList;
    }
    
}
