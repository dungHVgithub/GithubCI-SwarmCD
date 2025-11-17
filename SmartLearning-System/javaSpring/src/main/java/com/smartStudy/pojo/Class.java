/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author AN515-57
 */
@Entity
@Table(name = "class")
@NamedQueries({
    @NamedQuery(name = "Class.findAll", query = "SELECT c FROM Class c"),
    @NamedQuery(name = "Class.findById", query = "SELECT c FROM Class c WHERE c.id = :id"),
    @NamedQuery(name = "Class.findByClassName", query = "SELECT c FROM Class c WHERE c.className = :className"),
    @NamedQuery(name = "Class.findByCreatedAt", query = "SELECT c FROM Class c WHERE c.createdAt = :createdAt"),
    @NamedQuery(name = "Class.findByUpdatedAt", query = "SELECT c FROM Class c WHERE c.updatedAt = :updatedAt")})
public class Class implements Serializable {

    @Size(max = 255)
    @Column(name = "class_name")
    private String className;
    @JoinTable(name = "student_class", joinColumns = {
        @JoinColumn(name = "class_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "student_id", referencedColumnName = "user_id")})
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Student> studentList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "class1")
    @JsonIgnore
    private List<TeacherAssignment> teacherAssignmentList;
   
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @ManyToMany(mappedBy = "classList",fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Teacher> teacherList;

    @Transient
    private String teacherNames;

    @Transient
    private String studentNames;

    public Class() {
    }

    public Class(Integer id) {
        this.id = id;
    }

    public Class(Integer id, String className) {
        this.id = id;
        this.className = className;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Class)) {
            return false;
        }
        Class other = (Class) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.Class[ id=" + id + " ]";
    }

    public List<Teacher> getTeacherList() {
        return teacherList;
    }

    public void setTeacherList(List<Teacher> teacherList) {
        this.teacherList = teacherList;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public String getTeacherNames() {
        return teacherNames;
    }

    public void setTeacherNames(String teacherNames) {
        this.teacherNames = teacherNames;
    }

    public String getStudentNames() {
        return studentNames;
    }

    public void setStudentNames(String studentNames) {
        this.studentNames = studentNames;
    }

    public List<TeacherAssignment> getTeacherAssignmentList() {
        return teacherAssignmentList;
    }

    public void setTeacherAssignmentList(List<TeacherAssignment> teacherAssignmentList) {
        this.teacherAssignmentList = teacherAssignmentList;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

 
}
