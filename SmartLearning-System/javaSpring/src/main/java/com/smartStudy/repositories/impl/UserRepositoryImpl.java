package com.smartStudy.repositories.impl;

import com.smartStudy.pojo.User;
import com.smartStudy.repositories.UserRepository;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {
    private static final int PAGE_SIZE = 5;
    @Autowired
    private LocalSessionFactoryBean factory;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<User> getUsers(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<User> q = b.createQuery(User.class);
        Root root = q.from(User.class);
        q.select(root);
        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();
            String email = params.get("email");
            if (email != null && !email.isEmpty()) {
                predicates.add(b.like(root.get("email"), String.format("%%%s%%", email)));
            }
            String role = params.get("role");
            if (role != null && !role.isEmpty()) {
                predicates.add(b.like(root.get("role"), String.format("%%%s%%", role)));
            }
            q.where(predicates.toArray(Predicate[]::new));
        }
        Query query = s.createQuery(q);
        int page = 1;
        if (params != null) {
            String p = params.get("page");
            if (p != null && !p.isBlank() && !"null".equalsIgnoreCase(p)) {
                try {
                    page = Integer.parseInt(p);
                } catch (NumberFormatException ignored) {
                    // giữ nguyên page=1 nếu parse lỗi
                }
            }
        }
        page = Math.max(1, page);
        int start = (page - 1) * PAGE_SIZE;
        query.setFirstResult(start);
        query.setMaxResults(PAGE_SIZE);
        return query.getResultList();
    }

    @Override
    public long countUsers() {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery <Long> qr = cb.createQuery(Long.class);
        Root root = qr.from(User.class);
        qr.select(cb.count(root));
        return s.createQuery(qr).getSingleResult();
    }

    @Override
    public User getUserById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(User.class,id);
    }

    @Override
    public User getUserByMail(String email) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createNamedQuery("User.findByEmail", User.class);
        q.setParameter("email", email);
        try {
            return (User) q.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Lỗi khi truy vấn user theo email");
        }
    }

    @Override
    public void deleteUser(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        User u = this.getUserById(id);
        s.remove(u);
    }

    @Override
    public User updateUser(User u) {
        Session s = this.factory.getObject().getCurrentSession();
        if (u.getId() == null) {
            s.persist(u);
        } else {
            s.merge(u);
        }
        return u;
    }

    @Override
    public boolean authenticate(String email, String password) {
        User u = this.getUserByMail(email);
        return  this.passwordEncoder.matches(password,u.getPassword());
    }


}
