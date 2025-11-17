package com.smartStudy.repositories.impl;

import com.smartStudy.pojo.*;
import com.smartStudy.repositories.McqResponseRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class McqResponseRepositoryImpl implements McqResponseRepository {
    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    private Session currentSession() {
        return this.sessionFactory.getObject().getCurrentSession();
    }

    @Override
    public List<McqResponse> findBySubmission(Integer submissionId) {
        Session s = currentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<McqResponse> cq = cb.createQuery(McqResponse.class);
        Root<McqResponse> r = cq.from(McqResponse.class);
        List<Predicate> preds = new ArrayList<>();
        preds.add(cb.equal(r.get("exerciseSubmission").get("id"), submissionId));
        cq.select(r).where(preds.toArray(new Predicate[0]));
        // Tuỳ chọn: orderBy theo question id
        cq.orderBy(cb.asc(r.get("exerciseQuestion").get("id")));
        return s.createQuery(cq).getResultList();
    }

    @Override
    public McqResponse findOne(Integer submissionId, Integer questionId) {
        Session s = currentSession();
        McqResponsePK pk = new McqResponsePK();
        pk.setSubmissionId(submissionId);
        pk.setQuestionId(questionId);
        return s.get(McqResponse.class, pk);
    }

    @Override
    public McqResponse upsert(Integer submissionId, Integer questionId, Integer answerId) {
        Session s = currentSession();

        McqResponsePK pk = new McqResponsePK();
        pk.setSubmissionId(submissionId);
        pk.setQuestionId(questionId);

        McqResponse entity = s.get(McqResponse.class, pk);
        if (entity == null) {
            entity = new McqResponse();
            entity.setMcqResponsePK(pk);
            entity.setExerciseSubmission(s.get(ExerciseSubmission.class, submissionId));
            entity.setExerciseQuestion(s.get(ExerciseQuestion.class, questionId));
        }
        entity.setAnswerId(s.get(ExerciseAnswer.class, answerId)); // set FK tới đáp án đã chọn

        s.saveOrUpdate(entity);
        return entity;
    }

    @Override
    public void deleteOne(Integer submissionId, Integer questionId) {
        Session s = currentSession();
        McqResponse existing = findOne(submissionId, questionId);
        if (existing != null) {
            s.delete(existing);
        }
    }

    @Override
    public void deleteBySubmission(Integer submissionId) {
        Session s = currentSession();
        // HQL xoá hàng loạt
        s.createQuery("DELETE FROM McqResponse r WHERE r.submission.id = :sid")
                .setParameter("sid", submissionId)
                .executeUpdate();
    }
}
