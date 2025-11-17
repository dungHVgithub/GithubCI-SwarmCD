package com.smartStudy.repositories;

import com.smartStudy.pojo.McqResponse;

import java.util.List;

public interface McqResponseRepository {
    List<McqResponse> findBySubmission(Integer submissionId);
    McqResponse findOne(Integer submissionId, Integer questionId);
    McqResponse upsert(Integer submissionId, Integer questionId, Integer answerId);
    void deleteOne(Integer submissionId, Integer questionId);
    void deleteBySubmission(Integer submissionId);
}
