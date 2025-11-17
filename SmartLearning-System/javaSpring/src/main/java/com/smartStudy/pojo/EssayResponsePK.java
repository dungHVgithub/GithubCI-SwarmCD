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
public class EssayResponsePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "submission_id")
    private int submissionId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "question_id")
    private int questionId;

    public EssayResponsePK() {
    }

    public EssayResponsePK(int submissionId, int questionId) {
        this.submissionId = submissionId;
        this.questionId = questionId;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(int submissionId) {
        this.submissionId = submissionId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) submissionId;
        hash += (int) questionId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EssayResponsePK)) {
            return false;
        }
        EssayResponsePK other = (EssayResponsePK) object;
        if (this.submissionId != other.submissionId) {
            return false;
        }
        if (this.questionId != other.questionId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.EssayResponsePK[ submissionId=" + submissionId + ", questionId=" + questionId + " ]";
    }
    
}
