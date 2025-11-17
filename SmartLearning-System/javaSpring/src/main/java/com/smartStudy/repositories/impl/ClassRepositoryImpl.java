package com.smartStudy.repositories.impl;


import com.smartStudy.pojo.Class;
import com.smartStudy.pojo.Student;
import com.smartStudy.pojo.Subject;
import com.smartStudy.pojo.Teacher;
import com.smartStudy.repositories.ClassRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
public class ClassRepositoryImpl implements ClassRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @PersistenceContext
    private EntityManager em;

    private Session getCurrentSession() {
        return factoryBean.getObject().getCurrentSession();
    }

    @Override
    public List<Class> getCLasses(Map<String, String> params) {
        Session s = getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Class> q = b.createQuery(Class.class);
        Root root = q.from(Class.class);
        q.select(root);
        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();
            String classes = params.get("className");
            if (classes != null && !classes.isEmpty()) {
                predicates.add(b.like(root.get("className"), String.format("%%%s%%", classes)));
            }
            q.where(predicates.toArray(Predicate[]::new));
        }
        Query query = s.createQuery(q);
        return query.getResultList();
    }

    @Override
    public List<Teacher> getTeachersOfClass(int classId) {
        Session s = getCurrentSession();
        // HQL: join quan hệ ManyToMany/OneToMany c.teacherList
        String hql = """
            select distinct t
            from Class c
            join c.teacherList t
            where c.id = :classId
            """;
        Query<Teacher> q = s.createQuery(hql, Teacher.class);
        q.setParameter("classId", classId);
        return q.getResultList();    }

    @Override
    public List<Subject> getSubjectsOfTeacher(int teacherId) {
        Session s = getCurrentSession();
        // HQL: join quan hệ t.subjectList
        String hql = """
            select distinct s
            from Teacher t
            join t.subjectList s
            where t.userId = :teacherId
            order by s.title
            """;
        Query<Subject> q = s.createQuery(hql, Subject.class);
        q.setParameter("teacherId", teacherId);
        return q.getResultList();
    }

    @Override
    public Teacher getTeacherById(int teacherId) {
        Session s = getCurrentSession();
        // HQL: join fetch user (ManyToOne) + subjectList (collection)
        String hql = """
            select distinct t
            from Teacher t
            left join fetch t.user u
            left join fetch t.subjectList s
            where t.id = :id
            """;

        Query<Teacher> q = s.createQuery(hql, Teacher.class);
        q.setParameter("id", teacherId);
        return q.uniqueResult();
    }

    @Override
    public Class getClassById(int id) {
        Session s = getCurrentSession();
        return s.get(Class.class, id);
    }

    @Override
    public Class addOrUpdate(Class c) {
        Session session = getCurrentSession();

        // Lưu Class trước (nếu mới)
        if (c.getId() == null) {
            session.persist(c);
            session.flush(); // Đảm bảo lấy được c.getId()
        } else {
            session.merge(c);
            session.flush();
        }

        // Xóa hết liên kết cũ trong bảng trung gian (nếu là update)
        String delStudentSql = "DELETE FROM student_class WHERE class_id = :classId";
        session.createNativeQuery(delStudentSql)
                .setParameter("classId", c.getId())
                .executeUpdate();

        String delTeacherSql = "DELETE FROM teacher_class WHERE class_id = :classId";
        session.createNativeQuery(delTeacherSql)
                .setParameter("classId", c.getId())
                .executeUpdate();

        // Thêm mới các liên kết student - class
        if (c.getStudentList() != null) {
            for (Student s : c.getStudentList()) {
                String insSql = "INSERT INTO student_class (student_id, class_id) VALUES (:studentId, :classId)";
                session.createNativeQuery(insSql)
                        .setParameter("studentId", s.getUserId()) // hoặc s.getId() tùy mapping
                        .setParameter("classId", c.getId())
                        .executeUpdate();
            }
        }

        // Thêm mới các liên kết teacher - class
        if (c.getTeacherList() != null) {
            for (Teacher t : c.getTeacherList()) {
                String insSql = "INSERT INTO teacher_class (teacher_id, class_id) VALUES (:teacherId, :classId)";
                session.createNativeQuery(insSql)
                        .setParameter("teacherId", t.getUserId()) // hoặc t.getId() tùy mapping
                        .setParameter("classId", c.getId())
                        .executeUpdate();
            }
        }

        return c;
    }

    @Override
    public void deleteClass(int id) {
        Session session = getCurrentSession();
        Class c = this.getClassById(id);

        // Xóa quan hệ với giáo viên (2 chiều)
        if (c.getTeacherList() != null) {
            for (Teacher t : new ArrayList<>(c.getTeacherList())) {
                t.getClassList().remove(c);
            }
            c.getTeacherList().clear();
        }

        // Xóa quan hệ với học sinh (2 chiều)
        if (c.getStudentList() != null) {
            for (Student s : new ArrayList<>(c.getStudentList())) {
                s.getClassList().remove(c);
            }
            c.getStudentList().clear();
        }

        session.remove(c);
    }

    @Override
    public Integer totalStudentClass(Integer classId) {
        String sql = "SELECT COUNT(*) FROM student_class WHERE class_id = :classId";
        Number count = (Number) em.createNativeQuery(sql)
                .setParameter("classId", classId).getSingleResult();
        return count.intValue();
    }

    @Override
    public Integer totalTeacherClass(Integer classId) {
        String sql = "SELECT COUNT(*) FROM teacher_class WHERE class_id = :classId";
        Number count = (Number) em.createNativeQuery(sql)
                .setParameter("classId", classId).getSingleResult();

        return count.intValue();
    }
}
