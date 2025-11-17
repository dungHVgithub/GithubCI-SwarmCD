/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.pojo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author AN515-57
 */
@Entity
@Table(name = "exercise")
@NamedQueries({
    @NamedQuery(name = "Exercise.findAll", query = "SELECT e FROM Exercise e"),
    @NamedQuery(name = "Exercise.findById", query = "SELECT e FROM Exercise e WHERE e.id = :id"),
    @NamedQuery(name = "Exercise.findByTitle", query = "SELECT e FROM Exercise e WHERE e.title = :title"),
    @NamedQuery(name = "Exercise.findByType", query = "SELECT e FROM Exercise e WHERE e.type = :type"),
    @NamedQuery(name = "Exercise.findByCreatedAt", query = "SELECT e FROM Exercise e WHERE e.createdAt = :createdAt")})
public class Exercise implements Serializable {
    @Size(max = 200)
    @Column(name = "title")
    private String title;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "type")
    private String type;
    @JoinColumn(name = "created_by", referencedColumnName = "user_id")
    @ManyToOne
    private Teacher createdBy;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "exerciseId")
    @JsonIgnore
    private List<ExerciseQuestion> exerciseQuestionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "exerciseId")
    @JsonIgnore
    private List<ExerciseSubmission> exerciseSubmissionList;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @JoinColumn(name = "chapter_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @JsonIgnore
    private Chapter chapterId;
    public Exercise() {
    }

    public Exercise(Integer id) {
        this.id = id;
    }

    public Exercise(Integer id, String type) {
        this.id = id;
        this.type = type;
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

    public Chapter getChapterId() {
        return chapterId;
    }

    public void setChapterId(Chapter chapterId) {
        this.chapterId = chapterId;
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
        if (!(object instanceof Exercise)) {
            return false;
        }
        Exercise other = (Exercise) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.Exercise[ id=" + id + " ]";
    }


    public List<ExerciseQuestion> getExerciseQuestionList() {
        return exerciseQuestionList;
    }

    public void setExerciseQuestionList(List<ExerciseQuestion> exerciseQuestionList) {
        this.exerciseQuestionList = exerciseQuestionList;
    }

    public List<ExerciseSubmission> getExerciseSubmissionList() {
        return exerciseSubmissionList;
    }

    public void setExerciseSubmissionList(List<ExerciseSubmission> exerciseSubmissionList) {
        this.exerciseSubmissionList = exerciseSubmissionList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Teacher getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Teacher createdBy) {
        this.createdBy = createdBy;
    }
    
}
