package com.smartStudy.repositories.impl;
import com.smartStudy.pojo.Student;
import com.smartStudy.pojo.Teacher;
import com.smartStudy.repositories.StudentRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
        import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class StudentRepositoryImpl implements StudentRepository {
    @Autowired
    private LocalSessionFactoryBean factory;
    private Session getCurrentSession() {
        return factory.getObject().getCurrentSession();
    }

    @Override
    public List<Student> getStudents(Map<String, String> params) {
        Session s = getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Student> q = b.createQuery(Student.class);
        Root<Student> root = q.from(Student.class);
        q.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();
            String className = params.get("className"); // Đổi từ class_name thành className
            if (className != null && !className.isEmpty()) {
                Join<Student, Class> classJoin = root.join("classList");
                predicates.add(b.like(classJoin.get("className"), String.format("%%%s%%", className)));
            }
            q.where(predicates.toArray(Predicate[]::new));
        }
        Query query = s.createQuery(q);
        return query.getResultList();
    }

    @Override
    public Student getStudentById(int id) {
        Session s = getCurrentSession();
        return s.get(Student.class,id);
    }

    @Override
    public long quantityAllStudent() {
        Session s = getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Student> root = cq.from(Student.class);
        // Join với entity User ld "user"
        root.join("user");
        cq.select(cb.count(root));
        return s.createQuery(cq).getSingleResult();

    }

    @Override
    public long quantityStudentWeek() {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Student> root = cq.from(Student.class);
        Join<Student, Student> StudentJoin = root.join("user");
        LocalDate startOfWeek = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        Date startDate = Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Predicate datePredicate = cb.greaterThanOrEqualTo(StudentJoin.get("createdAt"), startDate);
        cq.select(cb.count(root)).where(datePredicate);
        return session.createQuery(cq).getSingleResult();
    }

    @Override
    public long quantityStudentMonth() {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Student> root = cq.from(Student.class);
        Join<Student, Student> StudentJoin = root.join("user");
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        Date startDate = Date.from(startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Predicate datePredicate = cb.greaterThanOrEqualTo(StudentJoin.get("createdAt"), startDate);
        cq.select(cb.count(root)).where(datePredicate);
        return session.createQuery(cq).getSingleResult();
    }

    @Override
    public Student findByUserId(Integer userId) {
        // Nếu dùng Hibernate Session
        String hql = "FROM Student s WHERE s.user.id = :userId";
        return   factory.getObject().getCurrentSession()
                .createQuery(hql, Student.class)
                .setParameter("userId", userId)
                .uniqueResult();
    }
}
