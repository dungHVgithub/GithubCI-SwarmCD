/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author AN515-57
 */
@Entity
@Table(name = "chapter_attachment")
@NamedQueries({
    @NamedQuery(name = "ChapterAttachment.findAll", query = "SELECT c FROM ChapterAttachment c"),
    @NamedQuery(name = "ChapterAttachment.findById", query = "SELECT c FROM ChapterAttachment c WHERE c.id = :id"),
    @NamedQuery(name = "ChapterAttachment.findByType", query = "SELECT c FROM ChapterAttachment c WHERE c.type = :type"),
    @NamedQuery(name = "ChapterAttachment.findByFilename", query = "SELECT c FROM ChapterAttachment c WHERE c.filename = :filename"),
    @NamedQuery(name = "ChapterAttachment.findByFilepath", query = "SELECT c FROM ChapterAttachment c WHERE c.filepath = :filepath"),
    @NamedQuery(name = "ChapterAttachment.findByUploadedAt", query = "SELECT c FROM ChapterAttachment c WHERE c.uploadedAt = :uploadedAt")})
public class ChapterAttachment implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 7)
    @Column(name = "type")
    private String type;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "filename")
    private String filename;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "filepath")
    private String filepath;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "extension")
    private String extension;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "uploaded_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadedAt;
    @JoinColumn(name = "chapter_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Chapter chapterId;

    public ChapterAttachment() {
    }

    public ChapterAttachment(Integer id) {
        this.id = id;
    }

    public ChapterAttachment(Integer id, String type, String filename, String filepath) {
        this.id = id;
        this.type = type;
        this.filename = filename;
        this.filepath = filepath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Date getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Date uploadedAt) {
        this.uploadedAt = uploadedAt;
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
        if (!(object instanceof ChapterAttachment)) {
            return false;
        }
        ChapterAttachment other = (ChapterAttachment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.ChapterAttachment[ id=" + id + " ]";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
    
}
