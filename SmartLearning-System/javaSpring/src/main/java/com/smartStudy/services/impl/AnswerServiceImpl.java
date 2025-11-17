package com.smartStudy.services.impl;

import com.smartStudy.pojo.ExerciseAnswer;
import com.smartStudy.pojo.ExerciseQuestion;
import com.smartStudy.repositories.AnswerRepository;
import com.smartStudy.services.AnswerService;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AnswerServiceImpl implements AnswerService {
    @Autowired
    private AnswerRepository repo;
    @Autowired
    private LocalSessionFactoryBean factory;

    private Session session() {
        return this.factory.getObject().getCurrentSession();
    }

    @Override
    public List<ExerciseAnswer> getAnswers(Map<String, String> params) {
        return repo.getAnswers(params);
    }

    @Override
    public long countAnswers(Map<String, String> params) {
        return repo.countAnswers(params);
    }

    @Override
    public ExerciseAnswer get(Integer id) {
        return repo.findById(id);
    }

    @Override
    public ExerciseAnswer create(ExerciseAnswer a, Integer questionId) {
        ExerciseQuestion q = session().get(ExerciseQuestion.class, questionId);
        if (q == null) return null;
        a.setQuestionId(q);
        return repo.save(a);
    }

    @Override
    public ExerciseAnswer update(Integer id, ExerciseAnswer patch, Integer questionId) {
        ExerciseAnswer old = repo.findById(id);
        if (old == null) return null;

        if (patch.getAnswerText() != null) old.setAnswerText(patch.getAnswerText());
        if (patch.getIsCorrect() != null) old.setIsCorrect(patch.getIsCorrect());

        if (questionId != null) {
            ExerciseQuestion q = session().get(ExerciseQuestion.class, questionId);
            if (q != null) old.setQuestionId(q);
        }
        return repo.save(old);
    }

    @Override
    public List<Object[]> findCorrectPairsByExercise(Integer exerciseId) {
        final String hql = """
                    select a.questionId.id, a.id
                    from ExerciseAnswer a
                    where a.questionId.exerciseId.id = :exId
                      and a.isCorrect = true
                """;
        return session()
                .createQuery(hql, Object[].class)
                .setParameter("exId", exerciseId)
                .getResultList();
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
    }
}
