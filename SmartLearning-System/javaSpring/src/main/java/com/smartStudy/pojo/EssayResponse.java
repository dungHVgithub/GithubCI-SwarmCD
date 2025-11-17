/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 *
 * @author AN515-57
 */
@Entity
@Table(name = "essay_response")
@NamedQueries({
    @NamedQuery(name = "EssayResponse.findAll", query = "SELECT e FROM EssayResponse e"),
    @NamedQuery(name = "EssayResponse.findBySubmissionId", query = "SELECT e FROM EssayResponse e WHERE e.essayResponsePK.submissionId = :submissionId"),
    @NamedQuery(name = "EssayResponse.findByQuestionId", query = "SELECT e FROM EssayResponse e WHERE e.essayResponsePK.questionId = :questionId")})
public class EssayResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected EssayResponsePK essayResponsePK;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "answer_essay")
    private String answerEssay;
    @JoinColumn(name = "question_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private ExerciseQuestion exerciseQuestion;
    @JoinColumn(name = "submission_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private ExerciseSubmission exerciseSubmission;

    public EssayResponse() {
    }

    public EssayResponse(EssayResponsePK essayResponsePK) {
        this.essayResponsePK = essayResponsePK;
    }

    public EssayResponse(EssayResponsePK essayResponsePK, String answerEssay) {
        this.essayResponsePK = essayResponsePK;
        this.answerEssay = answerEssay;
    }

    public EssayResponse(int submissionId, int questionId) {
        this.essayResponsePK = new EssayResponsePK(submissionId, questionId);
    }

    public EssayResponsePK getEssayResponsePK() {
        return essayResponsePK;
    }

    public void setEssayResponsePK(EssayResponsePK essayResponsePK) {
        this.essayResponsePK = essayResponsePK;
    }

    public String getAnswerEssay() {
        return answerEssay;
    }

    public void setAnswerEssay(String answerEssay) {
        this.answerEssay = answerEssay;
    }

    public ExerciseQuestion getExerciseQuestion() {
        return exerciseQuestion;
    }

    public void setExerciseQuestion(ExerciseQuestion exerciseQuestion) {
        this.exerciseQuestion = exerciseQuestion;
    }

    public ExerciseSubmission getExerciseSubmission() {
        return exerciseSubmission;
    }

    public void setExerciseSubmission(ExerciseSubmission exerciseSubmission) {
        this.exerciseSubmission = exerciseSubmission;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (essayResponsePK != null ? essayResponsePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EssayResponse)) {
            return false;
        }
        EssayResponse other = (EssayResponse) object;
        if ((this.essayResponsePK == null && other.essayResponsePK != null) || (this.essayResponsePK != null && !this.essayResponsePK.equals(other.essayResponsePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.EssayResponse[ essayResponsePK=" + essayResponsePK + " ]";
    }
    
}
