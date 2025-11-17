package com.smartStudy.repositories.impl;

import com.smartStudy.pojo.Chapter;
import com.smartStudy.repositories.ChapterRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class ChapterRepositoryImpl implements ChapterRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    private Session session() {
        return this.factoryBean.getObject().getCurrentSession();
    }


    @Override
    public List<Chapter> getChapters(Map<String, String> params) {
        Session s = session();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Chapter> q = b.createQuery(Chapter.class);
        Root<Chapter> root = q.from(Chapter.class);
        q.select(root);

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            // subjectId
            String subjectId = params.get("subjectId");
            if (subjectId != null && !subjectId.isBlank()) {
                predicates.add(b.equal(root.get("subjectId").get("id"), Integer.parseInt(subjectId)));
            }
            // keyword in title
            String keyword = params.get("keyword");
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(b.like(b.lower(root.get("title")), like));
            }
            // orderIndex (exact)
            String orderIndex = params.get("orderIndex");
            if (orderIndex != null && !orderIndex.isBlank()) {
                predicates.add(b.equal(root.get("orderIndex"), Integer.parseInt(orderIndex)));
            }
        }

        if (!predicates.isEmpty()) q.where(predicates.toArray(Predicate[]::new));

        // sort
        String sort = params != null ? params.getOrDefault("sort", "orderIndex") : "orderIndex";
        String dir = params != null ? params.getOrDefault("dir", "ASC") : "ASC";
        Path<?> sortPath = root.get(sort);
        q.orderBy("ASC".equalsIgnoreCase(dir) ? b.asc(sortPath) : b.desc(sortPath));

        Query query = s.createQuery(q);

        // pagination (0-based, an toàn)
        int page = tryParseInt(params != null ? params.get("page") : null, 0);
        int size = tryParseInt(params != null ? params.get("size") : null, 20);
        page = Math.max(0, page);
        size = Math.max(1, size);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    @Override
    public long countChapters(Map<String, String> params) {
        Session s = session();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Chapter> root = q.from(Chapter.class);
        q.select(b.count(root));

        List<Predicate> predicates = new ArrayList<>();
        if (params != null) {
            String subjectId = params.get("subjectId");
            if (subjectId != null && !subjectId.isBlank()) {
                predicates.add(b.equal(root.get("subjectId").get("id"), Integer.parseInt(subjectId)));
            }
            String keyword = params.get("keyword");
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(b.like(b.lower(root.get("title")), like));
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
    public Chapter findById(Integer id) {
        return session().get(Chapter.class, id);
    }

    @Override
    public Chapter save(Chapter c) {
        if (c.getId() == null) {
            session().persist(c);
            return c;
        }
        return (Chapter) session().merge(c);
    }

    @Override
    public void deleteById(Integer id) {
        Chapter c = findById(id);
        if (c != null) session().remove(c);
    }

    @Override
    public List<Chapter> chaptersBySubjectId(int subjectId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Chapter> criteria = builder.createQuery(Chapter.class);
        Root<Chapter> root = criteria.from(Chapter.class);
        //Truy vấn theo subjectId
        criteria.select(root).where(
                builder.equal(root.get("subjectId").get("id"), subjectId)
        );
        Query query = session.createQuery(criteria);
        return query.getResultList();
    }

    private static int tryParseInt(String s, int def) {
        try {
            return s == null ? def : Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
