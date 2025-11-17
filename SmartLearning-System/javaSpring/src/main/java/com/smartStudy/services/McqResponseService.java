package com.smartStudy.services;

import com.smartStudy.pojo.McqResponse;

import java.util.List;

public interface McqResponseService {
    List<McqResponse> findBySubmission(Integer submissionId);
    McqResponse findOne(Integer submissionId, Integer questionId);
    /**
     * Upsert 1 câu trả lời MCQ cho 1 câu hỏi trong 1 submission.
     * Validate: question thuộc exercise của submission; answer thuộc question.
     */
    McqResponse upsert(Integer submissionId, Integer questionId, Integer answerId);

    void deleteOne(Integer submissionId, Integer questionId);
    void deleteBySubmission(Integer submissionId);
}
