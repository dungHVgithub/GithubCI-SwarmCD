package com.smartStudy.services.impl;

import com.smartStudy.pojo.Chapter;
import com.smartStudy.pojo.Subject;
import com.smartStudy.repositories.ChapterRepository;
import com.smartStudy.services.ChapterService;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ChapterServiceImpl implements ChapterService {
    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private LocalSessionFactoryBean factory;


    private Session session()
    {
        return this.factory.getObject().getCurrentSession();
    }

    @Override
    public List<Chapter> getChapters(Map<String, String> params) {
        return this.chapterRepository.getChapters(params);
    }

    @Override
    public long countChapters(Map<String, String> params) {
        return this.chapterRepository.countChapters(params);
    }

    @Override
    public Chapter get(Integer id) {
        return this.chapterRepository.findById(id);
    }

    @Override
    public Chapter create(Chapter c, Integer subjectId) {
        if (subjectId != null) {
            Subject s = session().get(Subject.class, subjectId);
            if (s == null) return null;
            c.setSubjectId(s);
        }
        return this.chapterRepository.save(c);
    }

    @Override
    public Chapter update(Integer id, Chapter c, Integer subjectId) {
        Chapter old = chapterRepository.findById(id);
        if (old == null) return null;

        if (c.getTitle() != null)       old.setTitle(c.getTitle());
        if (c.getSummaryText() != null) old.setSummaryText(c.getSummaryText());
        if (c.getOrderIndex() != 0)     old.setOrderIndex(c.getOrderIndex());

        if (subjectId != null) {
            Subject s = session().get(Subject.class, subjectId);
            if (s != null) old.setSubjectId(s);
        }
        return chapterRepository.save(old);
    }

    @Override
    public void delete(Integer id) {
        this.chapterRepository.deleteById(id);
    }

    @Override
    public List<Chapter> chaptersBySubjectId(int subjectId) {
        return this.chapterRepository.chaptersBySubjectId(subjectId);
    }
}
