package com.smartStudy.repositories.impl;

import com.smartStudy.pojo.StudentSchedule;
import com.smartStudy.repositories.ScheduleRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ScheduleRepositoryImpl implements ScheduleRepository {
    @Autowired
    private LocalSessionFactoryBean sessionFactory;
    private Session getCurrentSession()
    {
        return this.sessionFactory.getObject().getCurrentSession();
    }
    @Override
    public StudentSchedule save(StudentSchedule s) {
        getCurrentSession().persist(s);
        return s;
    }

    @Override
    public StudentSchedule update(StudentSchedule s) {
        return (StudentSchedule) getCurrentSession().merge(s);
    }

    @Override
    public boolean delete(Integer id) {
        StudentSchedule found = getCurrentSession().get(StudentSchedule.class, id);
        if (found == null) return false;
        getCurrentSession().remove(found);
        return true;
    }

    @Override
    public Optional<StudentSchedule> findById(Integer id) {
        return Optional.ofNullable(getCurrentSession().get(StudentSchedule.class, id));
    }

    @Override
    public List<StudentSchedule> findByStudentAndDateRange(Integer studentUserId, LocalDate from, LocalDate to) {
        String hql = """
            select s
            from StudentSchedule s
            where s.studentId.userId = :sid
              and s.studyDate between :from and :to
            order by s.studyDate asc, s.startTime asc
            """;
        Query<StudentSchedule> q = getCurrentSession().createQuery(hql, StudentSchedule.class);
        q.setParameter("sid", studentUserId);
        q.setParameter("from", from);
        q.setParameter("to", to);
        return q.getResultList();
    }

    @Override
    public List<StudentSchedule> findByStudentAndDate(Integer studentUserId, LocalDate date) {
        String hql = """
            select s
            from StudentSchedule s
            where s.studentId.userId = :sid
              and s.studyDate = :d
            order by s.startTime asc
            """;
        Query<StudentSchedule> q = getCurrentSession().createQuery(hql, StudentSchedule.class);
        q.setParameter("sid", studentUserId);
        q.setParameter("d", date);
        return q.getResultList();
    }

    @Override
    public boolean existsOverlap(Integer studentId,
                                 LocalDate studyDate,
                                 LocalTime startTime,
                                 LocalTime endTime,
                                 Integer ignoreId) {
        String hql = """
            select count(s.id)
            from StudentSchedule s
            where s.studentId.userId = :sid
              and s.studyDate = :d
              and (:start < s.endTime and :end > s.startTime)
              and (:ignoreId is null or s.id <> :ignoreId)
            """;
        Query<Long> q = getCurrentSession().createQuery(hql, Long.class);
        q.setParameter("sid", studentId);
        q.setParameter("d", studyDate);
        q.setParameter("start", startTime);
        q.setParameter("end", endTime);
        q.setParameter("ignoreId", ignoreId);
        Long cnt = q.uniqueResult();
        return cnt != null && cnt > 0;
    }

    @Override
    public List<Integer> findStudentIdsByDate(LocalDate date) {
        Session s = getCurrentSession();
        String hql = """
            select distinct sc.studentId.id
            from StudentSchedule sc
            where sc.studyDate = :d
        """;
        Query<Integer> q = s.createQuery(hql, Integer.class);
        q.setParameter("d", date);
        return q.getResultList();
    }
}
