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
import java.util.List;

/**
 *
 * @author AN515-57
 */
@Entity
@Table(name = "exercise_answer")
@NamedQueries({
    @NamedQuery(name = "ExerciseAnswer.findAll", query = "SELECT e FROM ExerciseAnswer e"),
    @NamedQuery(name = "ExerciseAnswer.findById", query = "SELECT e FROM ExerciseAnswer e WHERE e.id = :id"),
    @NamedQuery(name = "ExerciseAnswer.findByAnswerText", query = "SELECT e FROM ExerciseAnswer e WHERE e.answerText = :answerText"),
    @NamedQuery(name = "ExerciseAnswer.findByIsCorrect", query = "SELECT e FROM ExerciseAnswer e WHERE e.isCorrect = :isCorrect")})
public class ExerciseAnswer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "answer_text")
    private String answerText;
    @Column(name = "is_correct")
    private Boolean isCorrect;
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ExerciseQuestion questionId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "answerId",fetch = FetchType.EAGER)
    @JsonIgnore
    private List<McqResponse> mcqResponseList;

    public ExerciseAnswer() {
    }

    public ExerciseAnswer(Integer id) {
        this.id = id;
    }

    public ExerciseAnswer(Integer id, String answerText) {
        this.id = id;
        this.answerText = answerText;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public ExerciseQuestion getQuestionId() {
        return questionId;
    }

    public void setQuestionId(ExerciseQuestion questionId) {
        this.questionId = questionId;
    }

    public List<McqResponse> getMcqResponseList() {
        return mcqResponseList;
    }

    public void setMcqResponseList(List<McqResponse> mcqResponseList) {
        this.mcqResponseList = mcqResponseList;
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
        if (!(object instanceof ExerciseAnswer)) {
            return false;
        }
        ExerciseAnswer other = (ExerciseAnswer) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.ExerciseAnswer[ id=" + id + " ]";
    }
    
}
