package com.smartStudy.repositories.impl;

import com.smartStudy.pojo.Exercise;
import com.smartStudy.repositories.ExerciseRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
public class ExcerciseRepositoryImpl implements ExerciseRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    private Session session()
    {
        return this.factory.getObject().getCurrentSession();
    }
    @Override
    public List<Exercise> getExercises(Map<String, String> params) {
        Session s = session();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Exercise> q = b.createQuery(Exercise.class);
        Root<Exercise> root = q.from(Exercise.class);
        q.select(root);

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            // keyword: tìm trong title hoặc description
            String keyword = params.get("keyword");
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(
                        b.or(
                                b.like(b.lower(root.get("title")), like),
                                b.like(b.lower(root.get("description")), like)
                        )
                );
            }

            // type: MCQ | ESSAY
            String type = params.get("type");
            if (type != null && !type.isBlank()) {
                predicates.add(b.equal(root.get("type"), type.trim().toUpperCase()));
            }

            // chapterId
            String chapterId = params.get("chapterId");
            if (chapterId != null && !chapterId.isBlank()) {
                predicates.add(b.equal(root.get("chapterId").get("id"), Integer.parseInt(chapterId)));
            }

            // createdBy (userId)
            String createdBy = params.get("createdBy");
            if (createdBy != null && !createdBy.isBlank()) {
                predicates.add(b.equal(root.get("createdBy").get("userId"), Integer.parseInt(createdBy)));
            }
        }

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }
        Query<Exercise> query = s.createQuery(q);
        return query.getResultList();
    }

    @Override
    public long countExercises(Map<String, String> params) {
        Session s = session();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Exercise> root = q.from(Exercise.class);
        q.select(b.count(root));
        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            String keyword = params.get("keyword");
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(
                        b.or(
                                b.like(b.lower(root.get("title")), like),
                                b.like(b.lower(root.get("description")), like)
                        )
                );
            }

            String type = params.get("type");
            if (type != null && !type.isBlank()) {
                predicates.add(b.equal(root.get("type"), type.trim().toUpperCase()));
            }

            String chapterId = params.get("chapterId");
            if (chapterId != null && !chapterId.isBlank()) {
                predicates.add(b.equal(root.get("chapterId").get("id"), Integer.parseInt(chapterId)));
            }

            String createdBy = params.get("createdBy");
            if (createdBy != null && !createdBy.isBlank()) {
                predicates.add(b.equal(root.get("createdBy").get("userId"), Integer.parseInt(createdBy)));
            }
        }

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        return s.createQuery(q).getSingleResult();
    }

    @Override
    public Exercise findById(Integer id) {
        return session().get(Exercise.class,id);
    }

    @Override
    public List<Exercise> findByChapterId(Integer chapterId) {
        String hql = "FROM Exercise e WHERE e.chapterId.id = :cid ORDER BY e.createdAt DESC";
        Query<Exercise> q = session().createQuery(hql,Exercise.class);
        q.setParameter("cid",chapterId);
        return q.getResultList();
    }

    @Override
    public Exercise save(Exercise ex) {
        if (ex.getId() == null) {
            session().persist(ex);
            return ex;
        }
        return (Exercise) session().merge(ex);
    }

    @Override
    public void deleteById(Integer id) {
        Exercise found = findById(id);
        if(found!=null) session().remove(found);
    }
    private static Integer tryParseInt(String s) {
        try { return s == null ? null : Integer.parseInt(s); }
        catch (NumberFormatException e) { return null; }
    }
}
