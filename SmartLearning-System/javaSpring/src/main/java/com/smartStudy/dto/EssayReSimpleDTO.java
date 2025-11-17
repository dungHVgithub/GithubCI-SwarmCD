package com.smartStudy.dto;

public class EssayReSimpleDTO {
    private String answerEssay;
    private QuestionDTO question;

    private SubmissionSimpleDTO submission;

    public EssayReSimpleDTO(String aw, QuestionDTO q, SubmissionSimpleDTO s)
    {
        this.answerEssay = aw;
        this.question = q;
        this.submission = s;
    }

    public String getAnswerEssay() {
        return answerEssay;
    }

    public void setAnswerEssay(String answerEssay) {
        this.answerEssay = answerEssay;
    }

    public QuestionDTO getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDTO question) {
        this.question = question;
    }

    public SubmissionSimpleDTO getSubmission() {
        return submission;
    }

    public void setSubmission(SubmissionSimpleDTO submission) {
        this.submission = submission;
    }
}
