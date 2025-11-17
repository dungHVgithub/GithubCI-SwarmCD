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
@Table(name = "chapter")
@NamedQueries({
    @NamedQuery(name = "Chapter.findAll", query = "SELECT c FROM Chapter c"),
    @NamedQuery(name = "Chapter.findById", query = "SELECT c FROM Chapter c WHERE c.id = :id"),
    @NamedQuery(name = "Chapter.findByTitle", query = "SELECT c FROM Chapter c WHERE c.title = :title"),
    @NamedQuery(name = "Chapter.findByOrderIndex", query = "SELECT c FROM Chapter c WHERE c.orderIndex = :orderIndex"),
    @NamedQuery(name = "Chapter.findByCreatedAt", query = "SELECT c FROM Chapter c WHERE c.createdAt = :createdAt"),
    @NamedQuery(name = "Chapter.findByUpdatedAt", query = "SELECT c FROM Chapter c WHERE c.updatedAt = :updatedAt")})
public class Chapter implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "title")
    private String title;
    @Lob
    @Size(max = 65535)
    @Column(name = "summary_text")
    private String summaryText;
    @Basic(optional = false)
    @NotNull
    @Column(name = "order_index")
    private int orderIndex;

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
    @JoinColumn(name = "subject_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Subject subjectId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chapterId")
    @JsonIgnore
    private List<ChapterAttachment> chapterAttachmentList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chapterId")
    @JsonIgnore
    private List<Exercise> exerciseList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chapterId")
    @JsonIgnore
    private List<ChapterProgress> chapterProgressList;

    public Chapter() {
    }

    public Chapter(Integer id) {
        this.id = id;
    }

    public Chapter(Integer id, String title, int orderIndex) {
        this.id = id;
        this.title = title;
        this.orderIndex = orderIndex;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getSummaryText() {
        return summaryText;
    }

    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
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

    public Subject getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Subject subjectId) {
        this.subjectId = subjectId;
    }

    public List<ChapterAttachment> getChapterAttachmentList() {
        return chapterAttachmentList;
    }

    public void setChapterAttachmentList(List<ChapterAttachment> chapterAttachmentList) {
        this.chapterAttachmentList = chapterAttachmentList;
    }

    public List<Exercise> getExerciseList() {
        return exerciseList;
    }

    public void setExerciseList(List<Exercise> exerciseList) {
        this.exerciseList = exerciseList;
    }

    public List<ChapterProgress> getChapterProgressList() {
        return chapterProgressList;
    }

    public void setChapterProgressList(List<ChapterProgress> chapterProgressList) {
        this.chapterProgressList = chapterProgressList;
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
        if (!(object instanceof Chapter)) {
            return false;
        }
        Chapter other = (Chapter) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.Chapter[ id=" + id + " ]";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
