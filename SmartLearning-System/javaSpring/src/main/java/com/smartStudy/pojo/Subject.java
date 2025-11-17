/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author AN515-57
 */
@Entity
@Table(name = "subject")
@NamedQueries({
        @NamedQuery(name = "Subject.findAll", query = "SELECT s FROM Subject s"),
        @NamedQuery(name = "Subject.findById", query = "SELECT s FROM Subject s WHERE s.id = :id"),
        @NamedQuery(name = "Subject.findByTitle", query = "SELECT s FROM Subject s WHERE s.title = :title"),
        @NamedQuery(name = "Subject.findByImage", query = "SELECT s FROM Subject s WHERE s.image = :image"),
        @NamedQuery(name = "Subject.findByCreatedAt", query = "SELECT s FROM Subject s WHERE s.createdAt = :createdAt"),
        @NamedQuery(name = "Subject.findByUpdatedAt", query = "SELECT s FROM Subject s WHERE s.updatedAt = :updatedAt")})
public class Subject implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "title")
    private String title;
    @Size(max = 255)
    @Column(name = "image")
    private String image;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "subjectId")
    @JsonIgnore
    private List<StudentSchedule> studentScheduleList;
    @ManyToMany(mappedBy = "subjectList")
    @JsonIgnore
    private List<Student> studentList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "subject")
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
    @ManyToMany(mappedBy = "subjectList", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Teacher> teacherList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "subjectId")
    @JsonIgnore
    private List<Chapter> chapterList;


    @Transient
    @JsonIgnore
    private MultipartFile file;
    @Transient
    private String teacherNames;

    @Transient
    private String studentNames;

    public Subject() {
    }

    public Subject(Integer id) {
        this.id = id;
    }

    public Subject(Integer id, String title,String image, String teacherNames) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.teacherNames = teacherNames;
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

    public List<Teacher> getTeacherList() {
        return teacherList;
    }

    public void setTeacherList(List<Teacher> teacherList) {
        this.teacherList = teacherList;
    }

    public List<Chapter> getChapterList() {
        return chapterList;
    }

    public void setChapterList(List<Chapter> chapterList) {
        this.chapterList = chapterList;
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
        if (!(object instanceof Subject)) {
            return false;
        }
        Subject other = (Subject) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.Subject[ id=" + id + " ]";
    }

    public String getTeacherNames() {
        return teacherNames;
    }

    public void setTeacherNames(String teacherNames) {
        this.teacherNames = teacherNames;
    }

    public String getStudentNames(){return studentNames;}
    public void setStudentNames(String studentNames){this.studentNames = studentNames;}

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }


    public List<TeacherAssignment> getTeacherAssignmentList() {
        return teacherAssignmentList;
    }

    public void setTeacherAssignmentList(List<TeacherAssignment> teacherAssignmentList) {
        this.teacherAssignmentList = teacherAssignmentList;
    }


    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }


    public List<StudentSchedule> getStudentScheduleList() {
        return studentScheduleList;
    }

    public void setStudentScheduleList(List<StudentSchedule> studentScheduleList) {
        this.studentScheduleList = studentScheduleList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
