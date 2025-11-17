package com.smartStudy.services.impl;

import com.smartStudy.pojo.Chapter;
import com.smartStudy.pojo.Exercise;
import com.smartStudy.pojo.Teacher;
import com.smartStudy.repositories.ExerciseRepository;
import com.smartStudy.services.ExcerciseService;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ExcerciseServiceImpl implements ExcerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private LocalSessionFactoryBean factory;

    private Session session() {
        return this.factory.getObject().getCurrentSession();
    }

    private Integer resolveChapterId(Exercise ex, Integer chapterId) {
        if (chapterId != null) return chapterId;
        if (ex != null && ex.getChapterId() != null) return ex.getChapterId().getId();
        return null;
    }

    private void attachChapterOrThrow(Exercise ex, Integer chapterId) {
        if (chapterId == null)
            throw new IllegalArgumentException("chapterId là bắt buộc");

        Chapter ch = session().get(Chapter.class, chapterId);
        if (ch == null)
            throw new IllegalArgumentException("chapterId không hợp lệ");

        ex.setChapterId(ch); // gán entity đã attach vào session
    }

    private void normalizeType(Exercise ex) {
        if (ex.getType() != null)
            ex.setType(ex.getType().toUpperCase()); // MCQ/ESSAY
    }

    @Override
    public List<Exercise> getExercises(Map<String, String> params) {
        return this.exerciseRepository.getExercises(params);
    }

    @Override
    public long countExercises(Map<String, String> params) {
        return this.exerciseRepository.countExercises(params);
    }

    @Override
    public Exercise get(Integer id) {
        return this.exerciseRepository.findById(id);
    }

    @Override
    public List<Exercise> findByChapterId(Integer cid) {
        return this.exerciseRepository.findByChapterId(cid);
    }

    // imports thêm: import java.util.Date;

    @Override
    public Exercise create(Exercise ex, Integer chapterId) {
        if (chapterId != null) {
            Chapter c = session().get(Chapter.class, chapterId);
            if (c == null) return null;
            ex.setChapterId(c);
        }
        if (ex.getCreatedBy() != null && ex.getCreatedBy().getUserId() != null) {
            Teacher t = session().get(Teacher.class, ex.getCreatedBy().getUserId());
            if (t == null)
                throw new IllegalArgumentException("createdBy không hợp lệ");
            ex.setCreatedBy(t);
        }
        if (ex.getCreatedAt() == null) ex.setCreatedAt(new Date());

        return this.exerciseRepository.save(ex);
    }

    @Override
    public Exercise update(Integer id, Exercise ex, Integer chapterId) {
        Exercise old = exerciseRepository.findById(id);
        if (old == null) return null;

        if (ex.getTitle() != null)       old.setTitle(ex.getTitle());
        if (ex.getDescription() != null) old.setDescription(ex.getDescription());
        if (ex.getType() != null)        old.setType(ex.getType().toUpperCase());

        Integer cid = resolveChapterId(ex, chapterId);
        if (cid != null) {
            Chapter ch = session().get(Chapter.class, cid);
            if (ch != null) old.setChapterId(ch);
        }
        if (ex.getCreatedBy() != null && ex.getCreatedBy().getUserId() != null) {
            Teacher t = session().get(Teacher.class, ex.getCreatedBy().getUserId());
            if (t == null)
                throw new IllegalArgumentException("createdBy không hợp lệ");
            old.setCreatedBy(t);
        }
        if (ex.getCreatedAt() != null) old.setCreatedAt(ex.getCreatedAt());

        return this.exerciseRepository.save(old);
    }


    @Override
    public void delete(Integer id) {
        this.exerciseRepository.deleteById(id);
    }
}
