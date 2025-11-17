package com.smartStudy.repositories.impl;

import com.smartStudy.pojo.Teacher;
import com.smartStudy.pojo.User;
import com.smartStudy.repositories.TeacherRepository;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class TeacherRepositoryImpl implements TeacherRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    private Session getCurrentSession() {
        return factory.getObject().getCurrentSession();
    }

    @Override
    public Teacher getTeacherById(int id) {
        Session s = getCurrentSession();
        return s.get(Teacher.class,id);
    }

    @Override
    public Long quantityAllTeacher() {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Teacher> root = cq.from(Teacher.class);
        // Join với entity Users qua field "user"
        root.join("user");
        cq.select(cb.count(root));
        return session.createQuery(cq).getSingleResult();
    }

    @Override
    public Long quantityTeacherWeek() {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Teacher> root = cq.from(Teacher.class);
        Join<Teacher, User> userJoin = root.join("user");

        LocalDate startOfWeek = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        Date startDate = Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Predicate datePredicate = cb.greaterThanOrEqualTo(userJoin.get("createdAt"), startDate);
        cq.select(cb.count(root)).where(datePredicate);

        return session.createQuery(cq).getSingleResult();
    }

    @Override
    public Long quantityTeacherMonth() {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Teacher> root = cq.from(Teacher.class);
        Join<Teacher, User> userJoin = root.join("user");

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        Date startDate = Date.from(startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Predicate datePredicate = cb.greaterThanOrEqualTo(userJoin.get("createdAt"), startDate);
        cq.select(cb.count(root)).where(datePredicate);

        return session.createQuery(cq).getSingleResult();
    }

    @Override
    public List<Teacher> getTeachers(Map<String, String> params) {
        Session s = getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Teacher> q = b.createQuery(Teacher.class);
        Root root = q.from(Teacher.class);
        q.select(root);
        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();
            String assignedClasses = params.get("assignedClasses");
            if (assignedClasses != null && !assignedClasses.isEmpty()) {
                predicates.add(b.like(root.get("assignedClasses"), String.format("%%%s%%", assignedClasses)));
            }
            q.where(predicates.toArray(Predicate[]::new));
        }
        Query query = s.createQuery(q);
        return query.getResultList();
    }

    @Override
    public Teacher findByUserId(Integer userId) {
        // Nếu dùng Hibernate Session
        String hql = "FROM Teacher t WHERE t.user.id = :userId";
        return   factory.getObject().getCurrentSession()
                .createQuery(hql, Teacher.class)
                .setParameter("userId", userId)
                .uniqueResult();
    }

}
