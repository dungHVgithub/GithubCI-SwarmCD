package com.smartStudy.services.impl;

import com.smartStudy.pojo.Student;
import com.smartStudy.pojo.StudentSchedule;
import com.smartStudy.pojo.Subject;
import com.smartStudy.repositories.ScheduleRepository;
import com.smartStudy.services.ScheduleService;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepo;

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    private Session session() {
        return Objects.requireNonNull(sessionFactory.getObject()).getCurrentSession();
    }

    // -------- helpers --------
    private void validateTime(LocalTime start, LocalTime end) {
        if (start == null || end == null)
            throw new IllegalArgumentException("startTime/endTime không được null");
        if (!start.isBefore(end))
            throw new IllegalArgumentException("startTime phải < endTime");
    }

    private Student getStudentByUserId(Integer studentId) {
        Query<Student> q = session().createQuery(
                "from Student s where s.userId = :uid", Student.class);
        q.setParameter("uid", studentId);
        Student st = q.uniqueResult();
        if (st == null)
            throw new IllegalArgumentException("Không tìm thấy Student với userId = " + studentId);
        return st;
    }

    private Subject getSubjectById(Integer subjectId) {
        Subject sub = session().get(Subject.class, subjectId);
        if (sub == null)
            throw new IllegalArgumentException("Không tìm thấy Subject với id = " + subjectId);
        return sub;
    }

    private void ensureNoOverlap(Integer studentUserId,
                                 LocalDate date,
                                 LocalTime start,
                                 LocalTime end,
                                 Integer ignoreId) {
        boolean overlapped = scheduleRepo.existsOverlap(studentUserId, date, start, end, ignoreId);
        if (overlapped)
            throw new IllegalStateException("Trùng giờ với lịch khác của học sinh trong ngày " + date);
    }

    // -------- API --------
    @Override
    public StudentSchedule create(Integer studentId,
                                  Integer subjectId,
                                  LocalDate studyDate,
                                  LocalTime startTime,
                                  LocalTime endTime,
                                  String note) {
        validateTime(startTime, endTime);
        ensureNoOverlap(studentId, studyDate, startTime, endTime, null);

        Student student = getStudentByUserId(studentId);
        Subject subject = getSubjectById(subjectId);

        StudentSchedule s = new StudentSchedule();
        s.setStudentId(student);
        s.setSubjectId(subject);
        s.setStudyDate(studyDate);
        s.setStartTime(startTime);
        s.setEndTime(endTime);
        s.setNote(note);

        return scheduleRepo.save(s);
    }

    @Override
    public StudentSchedule update(Integer id,
                                  Integer subjectId,
                                  LocalDate studyDate,
                                  LocalTime startTime,
                                  LocalTime endTime,
                                  String note) {
        StudentSchedule current = getRequired(id);

        // Nếu có đổi giờ/ngày → check hợp lệ + overlap
        if (studyDate != null) current.setStudyDate(studyDate);
        if (startTime != null) current.setStartTime(startTime);
        if (endTime != null) current.setEndTime(endTime);

        validateTime(current.getStartTime(), current.getEndTime());
        ensureNoOverlap(current.getStudentId().getUserId(),
                current.getStudyDate(), current.getStartTime(), current.getEndTime(), current.getId());

        if (subjectId != null) {
            Subject subject = getSubjectById(subjectId);
            current.setSubjectId(subject);
        }
        if (note != null) current.setNote(note);

        return scheduleRepo.update(current);
    }

    @Override
    public StudentSchedule updateTime(Integer id,
                                      LocalDate studyDate,
                                      LocalTime startTime,
                                      LocalTime endTime) {
        StudentSchedule current = getRequired(id);

        if (studyDate != null) current.setStudyDate(studyDate);
        if (startTime != null) current.setStartTime(startTime);
        if (endTime != null) current.setEndTime(endTime);

        validateTime(current.getStartTime(), current.getEndTime());
        ensureNoOverlap(current.getStudentId().getUserId(),
                current.getStudyDate(), current.getStartTime(), current.getEndTime(), current.getId());

        return scheduleRepo.update(current);
    }

    @Override
    public boolean delete(Integer id) {
        return scheduleRepo.delete(id);
    }

    @Override
    public StudentSchedule getRequired(Integer id) {
        return scheduleRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("StudentSchedule không tồn tại: id=" + id));
    }

    @Override
    public List<StudentSchedule> findByStudentAndDateRange(Integer studentUserId, LocalDate from, LocalDate to) {
        return scheduleRepo.findByStudentAndDateRange(studentUserId, from, to);
    }

    @Override
    public List<StudentSchedule> findByStudentAndDate(Integer studentUserId, LocalDate date) {
        return scheduleRepo.findByStudentAndDate(studentUserId, date);
    }

    @Override
    public List<Integer> findStudentIdsByDate(LocalDate date) {
        return this.scheduleRepo.findStudentIdsByDate(date);
    }
}
