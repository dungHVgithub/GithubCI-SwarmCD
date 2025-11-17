package com.smartStudy.services.impl;

import com.smartStudy.pojo.Exercise;
import com.smartStudy.pojo.ExerciseQuestion;
import com.smartStudy.repositories.QuestionRepository;
import com.smartStudy.services.QuestionService;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository repo;
    @Autowired
    private  LocalSessionFactoryBean factory;



    private Session session() {
        return this.factory.getObject().getCurrentSession();
    }

    @Override
    public List<ExerciseQuestion> getQuestions(Map<String, String> params) {
        return repo.getQuestions(params);
    }

    @Override
    public long countQuestions(Map<String, String> params) {
        return repo.countQuestions(params);
    }

    @Override
    public ExerciseQuestion get(Integer id) {
        return repo.findById(id);
    }

    @Override
    public List<ExerciseQuestion> findByExcerciseId(Integer qid) {
        return this.repo.findByExcerciseId(qid);
    }

    @Override
    public ExerciseQuestion create(ExerciseQuestion q, Integer exerciseId) {
        // gán Exercise theo id
        Exercise ex = session().get(Exercise.class, exerciseId);
        if (ex == null) return null;
        q.setExerciseId(ex);
        return repo.save(q);
    }

    @Override
    public ExerciseQuestion update(Integer id, ExerciseQuestion q, Integer exerciseId) {
        ExerciseQuestion old = repo.findById(id);
        if (old == null) return null;

        if (q.getQuestion() != null) old.setQuestion(q.getQuestion());
        if (q.getSolution() != null) old.setSolution(q.getSolution());
        if (q.getOrderIndex() != 0) old.setOrderIndex(q.getOrderIndex()); // lưu ý 0 là giá trị mặc định

        if (exerciseId != null) {
            Exercise ex = session().get(Exercise.class, exerciseId);
            if (ex != null) old.setExerciseId(ex);
        }

        return repo.save(old);
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
    }
}
