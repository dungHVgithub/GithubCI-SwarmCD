package com.smartStudy.services.impl;

import com.smartStudy.pojo.*;
import com.smartStudy.repositories.McqResponseRepository;
import com.smartStudy.services.McqResponseService;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class McqResponseServiceImpl  implements McqResponseService {
    @Autowired
    private McqResponseRepository repo;

    // Dùng luôn Session ở Service để validate quan hệ (đúng yêu cầu kỹ thuật)
    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    private Session currentSession() {
        return this.sessionFactory.getObject().getCurrentSession();
    }

    @Override
    public List<McqResponse> findBySubmission(Integer submissionId) {
        return this.repo.findBySubmission(submissionId);
    }

    @Override
    public McqResponse findOne(Integer submissionId, Integer questionId) {
        return this.repo.findOne(submissionId, questionId);
    }

    @Override
    public McqResponse upsert(Integer submissionId, Integer questionId, Integer answerId) {
        if (submissionId == null || questionId == null || answerId == null)
            throw new IllegalArgumentException("submissionId, questionId, answerId are required");

        // ===== Validate toàn vẹn bằng Session (Hibernate) =====
        Session s = currentSession();

        ExerciseSubmission sub = s.get(ExerciseSubmission.class, submissionId);
        if (sub == null)
            throw new IllegalArgumentException("Submission not found: " + submissionId);

        ExerciseQuestion q = s.get(ExerciseQuestion.class, questionId);
        if (q == null)
            throw new IllegalArgumentException("Question not found: " + questionId);

        ExerciseAnswer ans = s.get(ExerciseAnswer.class, answerId);
        if (ans == null)
            throw new IllegalArgumentException("Answer not found: " + answerId);

        Exercise ex = sub.getExerciseId();
        // (Tuỳ) kiểm tra exercise type là MCQ
        if (ex.getType() != null && !"MCQ".equalsIgnoreCase(ex.getType())) {
            throw new IllegalStateException("Exercise is not MCQ; cannot save MCQ response");
        }

        // Ủy quyền repo upsert (saveOrUpdate)
        return this.repo.upsert(submissionId, questionId, answerId);
    }

    @Override
    public void deleteOne(Integer submissionId, Integer questionId) {
        this.repo.deleteOne(submissionId, questionId);
    }

    @Override
    public void deleteBySubmission(Integer submissionId) {
        this.repo.deleteBySubmission(submissionId);
    }
}
