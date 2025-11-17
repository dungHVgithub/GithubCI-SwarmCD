package com.smartStudy.repositories.impl;


import com.smartStudy.pojo.ExerciseAnswer;
import com.smartStudy.repositories.AnswerRepository;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class AnswerRepositryImpl implements AnswerRepository {
    @Autowired
    private LocalSessionFactoryBean factory;
    private Session session() {
        return this.factory.getObject().getCurrentSession();
    }
    @Override
    public List<ExerciseAnswer> getAnswers(Map<String, String> params) {
        Session s = session();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<ExerciseAnswer> q = b.createQuery(ExerciseAnswer.class);
        Root<ExerciseAnswer> root = q.from(ExerciseAnswer.class);
        q.select(root);

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            // filter theo questionId
            String questionId = params.get("questionId");
            if (questionId != null && !questionId.isBlank()) {
                predicates.add(b.equal(root.get("questionId").get("id"), Integer.parseInt(questionId)));
            }

            // keyword trong answerText
            String keyword = params.get("keyword");
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(b.like(b.lower(root.get("answerText")), like));
            }

            // filter theo isCorrect (true/false)
            String isCorrect = params.get("isCorrect");
            if (isCorrect != null && !isCorrect.isBlank()) {
                predicates.add(b.equal(root.get("isCorrect"), Boolean.parseBoolean(isCorrect)));
            }
        }

        if (!predicates.isEmpty()) q.where(predicates.toArray(Predicate[]::new));

        // sort: mặc định id ASC (nếu bạn có orderIndex cho đáp án thì đổi thành orderIndex)
        String sort = params != null ? params.getOrDefault("sort", "id") : "id";
        String dir  = params != null ? params.getOrDefault("dir", "ASC") : "ASC";
        Path<?> sortPath = root.get(sort);
        q.orderBy("ASC".equalsIgnoreCase(dir) ? b.asc(sortPath) : b.desc(sortPath));

        Query<ExerciseAnswer> query = s.createQuery(q);

        // pagination 0-based
        int page = tryParseInt(params != null ? params.get("page") : null, 0);
        int size = tryParseInt(params != null ? params.get("size") : null, 50);
        page = Math.max(0, page);
        size = Math.max(1, size);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    @Override
    public long countAnswers(Map<String, String> params) {
        Session s = session();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<ExerciseAnswer> root = q.from(ExerciseAnswer.class);
        q.select(b.count(root));

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            String questionId = params.get("questionId");
            if (questionId != null && !questionId.isBlank()) {
                predicates.add(b.equal(root.get("questionId").get("id"), Integer.parseInt(questionId)));
            }
            String keyword = params.get("keyword");
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(b.like(b.lower(root.get("answerText")), like));
            }
            String isCorrect = params.get("isCorrect");
            if (isCorrect != null && !isCorrect.isBlank()) {
                predicates.add(b.equal(root.get("isCorrect"), Boolean.parseBoolean(isCorrect)));
            }
        }

        if (!predicates.isEmpty()) q.where(predicates.toArray(Predicate[]::new));

        return s.createQuery(q).getSingleResult();
    }

    @Override
    public ExerciseAnswer findById(Integer id) {
        return session().get(ExerciseAnswer.class, id);
    }

    @Override
    public ExerciseAnswer save(ExerciseAnswer a) {
        if (a.getId() == null) {
            session().persist(a);
            return a;
        }
        return (ExerciseAnswer) session().merge(a);
    }

    @Override
    public void deleteById(Integer id) {
        ExerciseAnswer found = findById(id);
        if (found != null) session().remove(found);
    }

    private static int tryParseInt(String s, int def) {
        try { return s == null ? def : Integer.parseInt(s); }
        catch (NumberFormatException e) { return def; }
    }
}
