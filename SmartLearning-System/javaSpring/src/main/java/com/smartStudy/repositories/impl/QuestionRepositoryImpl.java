package com.smartStudy.repositories.impl;

import com.smartStudy.pojo.Exercise;
import com.smartStudy.pojo.ExerciseQuestion;
import com.smartStudy.repositories.QuestionRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class QuestionRepositoryImpl implements QuestionRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    private Session session() {
        return this.factory.getObject().getCurrentSession();
    }

    @Override
    public List<ExerciseQuestion> getQuestions(Map<String, String> params) {
        Session s = session();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<ExerciseQuestion> q = b.createQuery(ExerciseQuestion.class);
        Root<ExerciseQuestion> root = q.from(ExerciseQuestion.class);
        q.select(root);

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            // filter by exerciseId
            String exerciseId = params.get("exerciseId");
            if (exerciseId != null && !exerciseId.isBlank()) {
                predicates.add(b.equal(root.get("exerciseId").get("id"), Integer.parseInt(exerciseId)));
            }

            // keyword in question
            String keyword = params.get("keyword");
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(b.like(b.lower(root.get("question")), like));
            }

            // filter by exact orderIndex (optional)
            String orderIndex = params.get("orderIndex");
            if (orderIndex != null && !orderIndex.isBlank()) {
                predicates.add(b.equal(root.get("orderIndex"), Integer.parseInt(orderIndex)));
            }
        }

        if (!predicates.isEmpty()) q.where(predicates.toArray(Predicate[]::new));

        // sort (default: orderIndex ASC, then id ASC để ổn định)
        String sort = params != null ? params.getOrDefault("sort", "orderIndex") : "orderIndex";
        String dir  = params != null ? params.getOrDefault("dir", "ASC") : "ASC";
        Path<?> sortPath = root.get(sort);
        if ("ASC".equalsIgnoreCase(dir)) {
            q.orderBy(b.asc(sortPath), b.asc(root.get("id")));
        } else {
            q.orderBy(b.desc(sortPath), b.desc(root.get("id")));
        }

        Query<ExerciseQuestion> query = s.createQuery(q);

        // pagination
        if (params != null) {
            Integer page = tryParseInt(params.get("page"));
            Integer size = tryParseInt(params.get("size"));
            if (page != null && size != null && size > 0) {
                query.setFirstResult(page * size);
                query.setMaxResults(size);
            }
        }

        return query.getResultList();
    }

    @Override
    public long countQuestions(Map<String, String> params) {
        Session s = session();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<ExerciseQuestion> root = q.from(ExerciseQuestion.class);
        q.select(b.count(root));

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            String exerciseId = params.get("exerciseId");
            if (exerciseId != null && !exerciseId.isBlank()) {
                predicates.add(b.equal(root.get("exerciseId").get("id"), Integer.parseInt(exerciseId)));
            }

            String keyword = params.get("keyword");
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(b.like(b.lower(root.get("question")), like));
            }

            String orderIndex = params.get("orderIndex");
            if (orderIndex != null && !orderIndex.isBlank()) {
                predicates.add(b.equal(root.get("orderIndex"), Integer.parseInt(orderIndex)));
            }
        }

        if (!predicates.isEmpty()) q.where(predicates.toArray(Predicate[]::new));

        return s.createQuery(q).getSingleResult();
    }

    @Override
    public ExerciseQuestion findById(Integer id) {
        return session().get(ExerciseQuestion.class, id);
    }

    @Override
    public List<ExerciseQuestion> findByExcerciseId(Integer excerciseId) {
        String hql = "FROM ExerciseQuestion q WHERE q.excerciseId.id = :qid ORDER BY q.createdAt DESC";
        Query<ExerciseQuestion> q = session().createQuery(hql,ExerciseQuestion.class);
        q.setParameter("qid",excerciseId);
        return q.getResultList();
    }


    @Override
    public ExerciseQuestion save(ExerciseQuestion q) {
        if (q.getId() == null) {
            session().persist(q);
            return q;
        }
        return (ExerciseQuestion) session().merge(q);
    }

    @Override
    public void deleteById(Integer id) {
        ExerciseQuestion found = findById(id);
        if (found != null) session().remove(found);
    }

    private static Integer tryParseInt(String s) {
        try { return s == null ? null : Integer.parseInt(s); }
        catch (NumberFormatException e) { return null; }
    }
}
