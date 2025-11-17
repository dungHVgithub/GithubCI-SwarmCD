package com.smartStudy.repositories.impl;

import com.smartStudy.pojo.ChapterAttachment;
import com.smartStudy.repositories.AttachmentRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class AttachmentRepositoryImpl implements AttachmentRepository {
    @Autowired
    private LocalSessionFactoryBean sessionFactoryBean;

    private Session cur()
    {
        SessionFactory ss = sessionFactoryBean.getObject();
        return ss.getCurrentSession();
    }

    @Override
    public ChapterAttachment save(ChapterAttachment a) {
        Session s = cur();
        if (a.getId() == null) {
            s.persist(a);
            return a;
        } else {
            s.merge(a);
            return a;
        }
    }

    @Override
    public ChapterAttachment findById(Integer id) {
        return cur().get(ChapterAttachment.class,id);
    }

    @Override
    public List<ChapterAttachment> findByChapterId(Integer chapterId) {
        String hql = """
            FROM ChapterAttachment a
            WHERE a.chapterId.id = :cid
            ORDER BY a.id DESC
            """;
        Query<ChapterAttachment> q = cur().createQuery(hql, ChapterAttachment.class);
        q.setParameter("cid", chapterId);
        return q.getResultList();
    }

    @Override
    public void delete(ChapterAttachment a) {
        Session s = cur();
        ChapterAttachment managed = a.getId() != null ? s.get(ChapterAttachment.class, a.getId()) : null;
        if (managed != null) s.remove(managed);
    }
}
