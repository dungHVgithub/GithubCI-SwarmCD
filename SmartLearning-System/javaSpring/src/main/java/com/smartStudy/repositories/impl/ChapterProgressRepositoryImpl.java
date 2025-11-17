package com.smartStudy.repositories.impl;

import com.smartStudy.pojo.Chapter;
import com.smartStudy.pojo.ChapterProgress;
import com.smartStudy.pojo.Student;
import com.smartStudy.pojo.User;
import com.smartStudy.repositories.ChapterProgressRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class ChapterProgressRepositoryImpl implements ChapterProgressRepository {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<ChapterProgress> getChapterProgressByStudent(Chapter chapter, User student) {
        Session session = getSession();
        Query<ChapterProgress> query = session.createQuery(
                "FROM ChapterProgress cp WHERE cp.chapterId = :chapter AND cp.studentId = :student",
                ChapterProgress.class
        );
        query.setParameter("chapter", chapter);
        query.setParameter("student", student);
        return query.getResultList();
    }

    @Override
    public void initializeChapterProgressForStudent(Integer studentId) {
        Session session = getSession();
        // Lấy Student (User)
        User student = session.get(User.class, studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student not found with ID: " + studentId);
        }

        // Lấy tất cả Chapter
        Query<Chapter> chapterQuery = session.createQuery("FROM Chapter", Chapter.class);
        List<Chapter> chapters = chapterQuery.getResultList();

        // Khởi tạo ChapterProgress cho mỗi Chapter nếu chưa tồn tại
        for (Chapter chapter : chapters) {
            Query<ChapterProgress> progressQuery = session.createQuery(
                    "FROM ChapterProgress cp WHERE cp.chapterId = :chapter AND cp.studentId = :student",
                    ChapterProgress.class
            );
            progressQuery.setParameter("chapter", chapter);
            progressQuery.setParameter("student", student);
            if (progressQuery.getResultList().isEmpty()) {
                ChapterProgress progress = new ChapterProgress();
                progress.setStudentId(student);
                progress.setChapterId(chapter);
                progress.setLastScore(BigDecimal.ZERO);
                progress.setPercent(0);
                progress.setUpdatedAt(new Date());
                session.save(progress);
            }
        }
    }

    @Override
    public ChapterProgress save(ChapterProgress progress) {
        Session session = getSession();
        if (progress.getStudentId() == null || progress.getChapterId() == null) {
            throw new IllegalArgumentException("StudentId and ChapterId must not be null");
        }
        progress.setUpdatedAt(new Date());
        session.saveOrUpdate(progress);
        return progress;
    }
}