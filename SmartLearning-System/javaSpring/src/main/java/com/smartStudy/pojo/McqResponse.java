/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.pojo;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author AN515-57
 */
@Entity
@Table(name = "mcq_response")
@NamedQueries({
    @NamedQuery(name = "McqResponse.findAll", query = "SELECT m FROM McqResponse m"),
    @NamedQuery(name = "McqResponse.findBySubmissionId", query = "SELECT m FROM McqResponse m WHERE m.mcqResponsePK.submissionId = :submissionId"),
    @NamedQuery(name = "McqResponse.findByQuestionId", query = "SELECT m FROM McqResponse m WHERE m.mcqResponsePK.questionId = :questionId")})
public class McqResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected McqResponsePK mcqResponsePK;
    @JoinColumn(name = "answer_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ExerciseAnswer answerId;
    @JoinColumn(name = "question_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private ExerciseQuestion exerciseQuestion;
    @JoinColumn(name = "submission_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private ExerciseSubmission exerciseSubmission;

    public McqResponse() {
    }

    public McqResponse(McqResponsePK mcqResponsePK) {
        this.mcqResponsePK = mcqResponsePK;
    }

    public McqResponse(int submissionId, int questionId) {
        this.mcqResponsePK = new McqResponsePK(submissionId, questionId);
    }

    public McqResponsePK getMcqResponsePK() {
        return mcqResponsePK;
    }

    public void setMcqResponsePK(McqResponsePK mcqResponsePK) {
        this.mcqResponsePK = mcqResponsePK;
    }

    public ExerciseAnswer getAnswerId() {
        return answerId;
    }

    public void setAnswerId(ExerciseAnswer answerId) {
        this.answerId = answerId;
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
        hash += (mcqResponsePK != null ? mcqResponsePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof McqResponse)) {
            return false;
        }
        McqResponse other = (McqResponse) object;
        if ((this.mcqResponsePK == null && other.mcqResponsePK != null) || (this.mcqResponsePK != null && !this.mcqResponsePK.equals(other.mcqResponsePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.McqResponse[ mcqResponsePK=" + mcqResponsePK + " ]";
    }
    
}
