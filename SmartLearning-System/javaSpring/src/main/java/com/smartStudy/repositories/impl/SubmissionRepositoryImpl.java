package com.smartStudy.repositories.impl;

import com.smartStudy.pojo.ExerciseSubmission;
import com.smartStudy.repositories.SubmissionRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class SubmissionRepositoryImpl implements SubmissionRepository {
    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<ExerciseSubmission> getExerciseSubmission(Map<String, String> params) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ExerciseSubmission> cq = cb.createQuery(ExerciseSubmission.class);
        Root<ExerciseSubmission> root = cq.from(ExerciseSubmission.class);
        cq.select(root);
        if (params != null && !params.isEmpty()) {
            List<Predicate> preds = new java.util.ArrayList<>();
            params.forEach((k, v) -> {
                if ("status".equalsIgnoreCase(k)) {
                    preds.add(cb.equal(root.get("status"), v));
                }
            });
            if (!preds.isEmpty()) cq.where(preds.toArray(new Predicate[0]));
        }

        return session.createQuery(cq).getResultList();
    }

    @Override
    public ExerciseSubmission findById(Integer id) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        return session.get(ExerciseSubmission.class, id);
    }

    @Override
    public List<ExerciseSubmission> findByExercise(Integer exerciseId, Integer studentId, String status) {
        Session s = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<ExerciseSubmission> cq = cb.createQuery(ExerciseSubmission.class);
        Root<ExerciseSubmission> r = cq.from(ExerciseSubmission.class);
        cq.select(r);

        List<Predicate> preds = new java.util.ArrayList<>();
        // JOIN theo quan hệ:
        // exercise: ManyToOne<Exercise> exercise;  -> r.get("exercise").get("id")
        // student:  ManyToOne<Student>  student;   -> r.get("student").get("userId")
        preds.add(cb.equal(r.get("exerciseId").get("id"), exerciseId));

        if (studentId != null) {
            preds.add(cb.equal(r.get("student").get("userId"), studentId));
            // ^ dùng "userId" vì Student PK là user_id (đúng với mapping bạn đã sửa)
        }
        if (status != null && !status.trim().isEmpty()) {
            preds.add(cb.equal(r.get("status"), status.trim()));
        }

        cq.where(preds.toArray(new Predicate[0]));

        // (tuỳ chọn) sort theo submittedAt desc trước
        cq.orderBy(cb.desc(r.get("submittedAt")));
        return s.createQuery(cq).getResultList();
    }

    @Override
    public ExerciseSubmission save(ExerciseSubmission es) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        session.saveOrUpdate(es);
        return es;
    }

    @Override
    public void deleteById(Integer id) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        ExerciseSubmission es = session.get(ExerciseSubmission.class, id);
        if (es != null) {
            session.delete(es);
        }
    }
}
