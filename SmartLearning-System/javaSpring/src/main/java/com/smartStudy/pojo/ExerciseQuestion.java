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
@Table(name = "exercise_question")
@NamedQueries({
    @NamedQuery(name = "ExerciseQuestion.findAll", query = "SELECT e FROM ExerciseQuestion e"),
    @NamedQuery(name = "ExerciseQuestion.findById", query = "SELECT e FROM ExerciseQuestion e WHERE e.id = :id"),
    @NamedQuery(name = "ExerciseQuestion.findByOrderIndex", query = "SELECT e FROM ExerciseQuestion e WHERE e.orderIndex = :orderIndex")})
public class ExerciseQuestion implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "order_index")
    private int orderIndex;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "question")
    private String question;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "exerciseQuestion")
    @JsonIgnore
    private List<EssayResponse> essayResponseList;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Lob
    @Column(name = "solution")
    private String solution;
    @JoinColumn(name = "exercise_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Exercise exerciseId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionId")
    @JsonIgnore
    private List<ExerciseAnswer> exerciseAnswerList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "exerciseQuestion", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<McqResponse> mcqResponseList;

    public ExerciseQuestion() {
    }

    public ExerciseQuestion(Integer id) {
        this.id = id;
    }

    public ExerciseQuestion(Integer id, String question, int orderIndex) {
        this.id = id;
        this.question = question;
        this.orderIndex = orderIndex;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Exercise getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Exercise exerciseId) {
        this.exerciseId = exerciseId;
    }

    public List<ExerciseAnswer> getExerciseAnswerList() {
        return exerciseAnswerList;
    }

    public void setExerciseAnswerList(List<ExerciseAnswer> exerciseAnswerList) {
        this.exerciseAnswerList = exerciseAnswerList;
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
        if (!(object instanceof ExerciseQuestion)) {
            return false;
        }
        ExerciseQuestion other = (ExerciseQuestion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.smartStudy.pojo.ExerciseQuestion[ id=" + id + " ]";
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<EssayResponse> getEssayResponseList() {
        return essayResponseList;
    }

    public void setEssayResponseList(List<EssayResponse> essayResponseList) {
        this.essayResponseList = essayResponseList;
    }
}
