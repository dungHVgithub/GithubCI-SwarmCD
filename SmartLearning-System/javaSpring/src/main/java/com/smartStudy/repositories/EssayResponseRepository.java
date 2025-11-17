package com.smartStudy.repositories;

import com.smartStudy.dto.EssayReSimpleDTO;
import com.smartStudy.pojo.EssayResponse;
import java.util.List;

public interface EssayResponseRepository {
    List<EssayResponse> findBySubmission(Integer submissionId);
    List <EssayReSimpleDTO> findByExercise(Integer exerciseId);
    EssayResponse findOne(Integer submissionId, Integer questionId);
    EssayResponse upsert(Integer submissionId, Integer questionId, String answerEssay);
    void deleteOne(Integer submissionId, Integer questionId);
    void deleteBySubmission(Integer submissionId);
}
