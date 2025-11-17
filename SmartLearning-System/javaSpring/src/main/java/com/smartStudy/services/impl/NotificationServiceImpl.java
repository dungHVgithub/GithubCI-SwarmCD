package com.smartStudy.services.impl;

import com.smartStudy.pojo.Notification;
import com.smartStudy.repositories.NotificationRepository;
import com.smartStudy.services.NotifcationService;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotifcationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    private Session session() {
        return factoryBean.getObject().getCurrentSession();
    }

    @Override
    public Notification saveOrUpdate(Notification no) {
        return this.notificationRepository.addOrUpdate(no);
    }

    @Override
    public List<Notification> getNotifications(Map<String, String> params) {
        return this.notificationRepository.getNotifications(params);
    }

    @Override
    @Transactional
    public int markAllRead(Integer studentId, Integer teacherId, String type) {
        if (studentId == null && teacherId == null)
            throw new IllegalArgumentException("studentId or teacherId is required");

        StringBuilder hql = new StringBuilder("update Notification n set n.isReaded = true where 1=1");
        Map<String, Object> params = new HashMap<>();

        if (studentId != null) {
            // n.studentId là ManyToOne -> lọc theo khóa của Student
            hql.append(" and n.studentId.userId = :sid");
            params.put("sid", studentId);
        }
        if (teacherId != null) {
            // n.teacherId là ManyToOne -> lọc theo khóa của Teacher
            hql.append(" and n.teacherId.userId = :tid");
            params.put("tid", teacherId);
        }
        if (type != null && !type.isBlank()) {
            hql.append(" and n.type = :type");
            params.put("type", type.trim());
        }

        Query<?> q = session().createQuery(hql.toString());
        params.forEach(q::setParameter);
        return q.executeUpdate();
    }

}


