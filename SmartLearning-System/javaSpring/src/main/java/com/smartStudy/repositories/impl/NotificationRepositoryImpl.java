package com.smartStudy.repositories.impl;

import com.smartStudy.pojo.Exercise;
import com.smartStudy.pojo.Notification;
import com.smartStudy.repositories.NotificationRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
public class NotificationRepositoryImpl implements NotificationRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;
    private Session getCurrentSession() {
        return factoryBean.getObject().getCurrentSession();
    }

    @Override
    public Notification addOrUpdate(Notification notification) {
        Session s = getCurrentSession();
        if(notification.getId() == null)
        {
            s.persist(notification);
        }
        else
        {
            s.merge(notification);
        }
        return notification;
    }

    @Override
    public List<Notification> getNotifications(Map<String, String> params) {
        Session s = getCurrentSession();

        // Criteria
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Notification> cq = cb.createQuery(Notification.class);
        Root<Notification> root = cq.from(Notification.class);

        List<Predicate> where = new ArrayList<>();

        // ---- Filters ----
        Integer teacherId = parseInt(params.get("teacherId"));
        if (teacherId != null) {
            where.add(cb.equal(root.get("teacherId").get("userId"), teacherId));
            // nếu PK của Teacher là "id": root.get("teacherId").get("id")
        }

        Integer studentId = parseInt(params.get("studentId"));
        if (studentId != null) {
            where.add(cb.equal(root.get("studentId").get("userId"), studentId));
            // nếu PK của Student là "id": root.get("studentId").get("id")
        }
        Boolean isReaded = parseBool(params.get("isReaded"));
        if (isReaded != null) {
            if (Boolean.TRUE.equals(isReaded)) {
                // chỉ lấy đã đọc
                where.add(cb.isTrue(root.get("isReaded")));
            } else {
                // lấy chưa đọc: false HOẶC NULL (coi NULL như chưa đọc)
                where.add(cb.or(cb.isFalse(root.get("isReaded")), cb.isNull(root.get("isReaded"))));
            }
        }
        String type = trimToNull(params.get("type"));
        if (type != null) {
            where.add(cb.equal(root.get("type"), type));
        }

        Integer beforeId = parseInt(params.get("beforeId"));
        if (beforeId != null) {
            // id < beforeId (phân trang kiểu load-more)
            where.add(cb.lt(root.get("id"), beforeId));
        }

        if (!where.isEmpty()) {
            cq.where(where.toArray(new Predicate[0]));
        }

        // ---- Sort ----
        String sortBy = sanitizeSortBy(trimToNull(params.get("sortBy"))); // "id" | "sentAt"
        String order  = sanitizeOrder(trimToNull(params.get("order")));   // "asc" | "desc"
        if ("asc".equalsIgnoreCase(order)) {
            cq.orderBy(cb.asc(root.get(sortBy)));
        } else {
            cq.orderBy(cb.desc(root.get(sortBy)));
        }

        // ---- Limit ----
        int limit = clamp(parseInt(params.get("limit")), 1, 50);
        if (limit == 0) limit = 10;

        return s.createQuery(cq)
                .setMaxResults(limit)
                .getResultList();
    }
    // ===== helpers =====

    private static Integer parseInt(String s) {
        if (s == null) return null;
        try { return Integer.valueOf(s.trim()); } catch (NumberFormatException e) { return null; }
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String v = s.trim();
        return v.isEmpty() ? null : v;
    }
    // parseBool mạnh hơn: true/false/1/0/yes/no
    private static Boolean parseBool(String s) {
        if (s == null) return null;
        String v = s.trim().toLowerCase(java.util.Locale.ROOT);
        if ("true".equals(v) || "1".equals(v) || "t".equals(v) || "yes".equals(v))  return Boolean.TRUE;
        if ("false".equals(v) || "0".equals(v) || "f".equals(v) || "no".equals(v)) return Boolean.FALSE;
        return null;
    }


    private static int clamp(Integer v, int min, int max) {
        if (v == null) return 0;
        return Math.max(min, Math.min(max, v));
    }

    private static String sanitizeSortBy(String sortBy) {
        // Chỉ cho phép 2 cột an toàn
        if ("sentAt".equals(sortBy)) return "sentAt";
        return "id";
        // Nếu muốn cho phép "title"… cần whitelist tương tự.
    }

    private static String sanitizeOrder(String order) {
        if ("asc".equalsIgnoreCase(order)) return "asc";
        return "desc";
    }
}
