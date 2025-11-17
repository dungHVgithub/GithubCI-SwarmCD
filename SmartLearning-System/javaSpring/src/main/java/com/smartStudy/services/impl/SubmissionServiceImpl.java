package com.smartStudy.services.impl;

import com.smartStudy.pojo.*;
import com.smartStudy.repositories.*;
import com.smartStudy.services.AnswerService;
import com.smartStudy.services.SubmissionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class SubmissionServiceImpl implements SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepo;

    @Autowired
    private ExerciseRepository exerciseRepo;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private McqResponseRepository mcqResponseRepo;

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private LocalSessionFactoryBean factory;

    private Session session()
    {
        return this.factory.getObject().getCurrentSession();
    }
    @PersistenceContext
    private EntityManager em; // <-- dùng để lấy managed reference

    public SubmissionServiceImpl(SubmissionRepository submissionRepo, ExerciseRepository exerciseRepo) {
        this.submissionRepo = submissionRepo;
        this.exerciseRepo = exerciseRepo;
    }

    @Override
    public List<ExerciseSubmission> getExerciseSubmission(Map<String, String> params) {
        return this.submissionRepo.getExerciseSubmission(params);
    }

    @Override
    public ExerciseSubmission findById(Integer id) {
        return this.submissionRepo.findById(id);
    }

    @Override
    public List<ExerciseSubmission> findByExercise(Integer exerciseId, Integer studentId, String status) {
        return this.submissionRepo.findByExercise(exerciseId, studentId, status);
    }

    @Override
    public ExerciseSubmission create(ExerciseSubmission req, Integer exerciseId, Integer studentId) {
        // KHÔNG gán đối tượng transient từ req
        ExerciseSubmission s = new ExerciseSubmission();
        s.setStatus(req.getStatus());
        s.setSubmittedAt(req.getSubmittedAt());    // null nếu "Lưu bài", có giá trị nếu "Nộp bài"
        // grade/feedback do Teacher set sau – không nhận từ Student ở đây

        // Lấy managed reference thay vì new Student()/new Exercise()
        Exercise exerciseRef = em.getReference(Exercise.class, exerciseId);
        Student studentRef = em.getReference(Student.class, studentId);
        s.setExerciseId(exerciseRef);
        s.setStudent(studentRef);

        return this.submissionRepo.save(s);
    }

    @Override
    public ExerciseSubmission update(Integer id, ExerciseSubmission req, Integer exerciseId, Integer studentId) {
        ExerciseSubmission existing = this.submissionRepo.findById(id);
        if (existing == null) return null;

        // cập nhật các field cho phép
        if (req.getStatus() != null) existing.setStatus(req.getStatus());
        if (req.getSubmittedAt() != null) existing.setSubmittedAt(req.getSubmittedAt());
        if (req.getFeedback() != null) existing.setFeedback(req.getFeedback()); // chỉ Teacher nên set ở API phù hợp
        if (req.getGrade() != null) existing.setGrade(req.getGrade());       // "

        if (exerciseId != null) {
            existing.setExerciseId(em.getReference(Exercise.class, exerciseId));
        }
        if (studentId != null) {
            existing.setStudent(em.getReference(Student.class, studentId));
        }
        if ("COMPLETED".equalsIgnoreCase(existing.getStatus())) {
            String type = existing.getExerciseId() != null ? existing.getExerciseId().getType() : null;
            if ("MCQ".equalsIgnoreCase(type)) {
                int score = autoGradeMcq(existing.getId());   // sẽ set grade bên trong
                // Nếu bài này chỉ là MCQ-only, có thể chốt luôn:
                existing.setStatus("GRADED");
            }
        }
        // BUG cũ: return save(es) -> phải save(existing)
        return this.submissionRepo.save(existing);
    }

    @Override
    public int autoGradeMcq(Integer submissionId) {
        ExerciseSubmission sub = this.submissionRepo.findById(submissionId);
        if (sub == null) throw new IllegalArgumentException("Submission not found: " + submissionId);

        Exercise exercise = sub.getExerciseId();
        if (exercise == null) throw new IllegalStateException("Submission has no exercise");
        Integer exId = exercise.getId();

        // 1) Lấy map đáp án đúng theo questionId
        List<Object[]> pairs = answerService.findCorrectPairsByExercise(exId); // (questionId, correctAnswerId)
        Map<Integer, Set<Integer>> correctByQuestion = new HashMap<>();
        for (Object[] row : pairs) {
            Integer qid   = (Integer) row[0];
            Integer ansId = (Integer) row[1];
            correctByQuestion.computeIfAbsent(qid, k -> new HashSet<>()).add(ansId);
        }

        // 2) Lấy tổng số câu hỏi trong exercise (KHÔNG dựa vào số đáp án đúng)
        //    -> dùng HQL nhanh gọn qua Session
        Long totalQ = session()
                .createQuery("select count(q) from ExerciseQuestion q where q.exerciseId.id = :exId", Long.class)
                .setParameter("exId", exId)
                .getSingleResult();
        int totalQuestions = totalQ == null ? 0 : totalQ.intValue();

        // 3) Lấy các lựa chọn của student cho submission hiện tại
        List<McqResponse> responses = mcqResponseRepo.findBySubmission(submissionId);

        // 4) Đếm số câu TRẢ LỜI ĐÚNG
        int correct = 0;
        for (McqResponse r : responses) {
            Integer qid = r.getExerciseQuestion() != null ? r.getExerciseQuestion().getId()
                    : (r.getExerciseQuestion() != null ? r.getExerciseQuestion().getId() : null);
            Integer chosenId = r.getAnswerId() != null ? r.getAnswerId().getId()
                    : (r.getAnswerId() != null ? r.getAnswerId().getId() : null);
            if (qid == null || chosenId == null) continue;

            Set<Integer> correctSet = correctByQuestion.get(qid);
            if (correctSet != null && correctSet.contains(chosenId)) {
                correct += 1;
            }
        }
        // 5) Chuẩn hoá về thang 10 điểm (ví dụ 1/2 câu đúng = 5 điểm)
        int grade10 = 0;
        if (totalQuestions > 0) {
            grade10 = (int) Math.round((correct * 10.0) / totalQuestions); // tránh chia nguyên
        }
        sub.setGrade(BigDecimal.valueOf(grade10));
        this.submissionRepo.save(sub);
        return grade10;
    }


    @Override
    public void deleteById(Integer id) {
        this.submissionRepo.deleteById(id);
    }
}
