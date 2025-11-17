package com.smartStudy.repositories.impl;

import com.smartStudy.dto.EssayReSimpleDTO;
import com.smartStudy.pojo.EssayResponse;
import com.smartStudy.pojo.EssayResponsePK;
import com.smartStudy.pojo.ExerciseQuestion;
import com.smartStudy.pojo.ExerciseSubmission;
import com.smartStudy.repositories.EssayResponseRepository;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class EssayResponseRepositoryImpl implements EssayResponseRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    private Session currentSession() {
        return this.sessionFactory.getObject().getCurrentSession();
    }

    @Override
    public List<EssayResponse> findBySubmission(Integer submissionId) {
        Session s = currentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<EssayResponse> cq = cb.createQuery(EssayResponse.class);
        Root<EssayResponse> r = cq.from(EssayResponse.class);

        List<Predicate> preds = new ArrayList<>();
        preds.add(cb.equal(r.get("exerciseSubmission").get("id"), submissionId));

        cq.select(r).where(preds.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(r.get("exerciseQuestion").get("id"))); // tuỳ chọn

        return s.createQuery(cq).getResultList();
    }

    @Override
    public List<EssayReSimpleDTO> findByExercise(Integer exerciseId) {
        final String hql = """
                    select new com.smartStudy.dto.EssayReSimpleDTO(
                        r.answerEssay,
                        new com.smartStudy.dto.QuestionDTO(
                            q.id,
                            q.orderIndex,
                            q.question,
                            q.solution,
                            e.id           
                        ),
                        new com.smartStudy.dto.SubmissionSimpleDTO(
                            es.id,
                            es.status,
                            es.feedback,
                            es.grade,
                            new com.smartStudy.dto.StudentSimpleDTO(
                                st.id,
                                new com.smartStudy.dto.UserSimpleDTO(u.id, u.name,u.email)  
                            )
                        )
                    )
                    from EssayResponse r
                    join r.exerciseSubmission es
                    join es.exerciseId e            
                    join r.exerciseQuestion q   
                    join es.student st
                    join st.user u
                    where e.id = :exerciseId
                """;
        return currentSession()
                .createQuery(hql, EssayReSimpleDTO.class)
                .setParameter("exerciseId", exerciseId)
                .getResultList();
    }

    @Override
    public EssayResponse findOne(Integer submissionId, Integer questionId) {
        Session s = currentSession();
        EssayResponsePK pk = new EssayResponsePK();
        pk.setSubmissionId(submissionId);
        pk.setQuestionId(questionId);
        return s.get(EssayResponse.class, pk);
    }

    @Override
    public EssayResponse upsert(Integer submissionId, Integer questionId, String answerEssay) {
        Session s = currentSession();

        EssayResponsePK pk = new EssayResponsePK();
        pk.setSubmissionId(submissionId);
        pk.setQuestionId(questionId);

        EssayResponse entity = s.get(EssayResponse.class, pk);
        if (entity == null) {
            entity = new EssayResponse();
            entity.setEssayResponsePK(pk);
            entity.setExerciseSubmission(s.get(ExerciseSubmission.class, submissionId));
            entity.setExerciseQuestion(s.get(ExerciseQuestion.class, questionId));
        }
        entity.setAnswerEssay(answerEssay);

        s.saveOrUpdate(entity);
        return entity;
    }

    @Override
    public void deleteOne(Integer submissionId, Integer questionId) {
        EssayResponse existing = findOne(submissionId, questionId);
        if (existing != null) {
            currentSession().delete(existing);
        }
    }

    @Override
    public void deleteBySubmission(Integer submissionId) {
        currentSession().createQuery(
                        "DELETE FROM EssayResponse r WHERE r.submission.id = :sid")
                .setParameter("sid", submissionId)
                .executeUpdate();
    }
}
