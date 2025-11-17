package com.smartStudy.repositories;

import com.smartStudy.pojo.StudentSchedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {

    // Create
    StudentSchedule save(StudentSchedule s);

    // Update
    StudentSchedule update(StudentSchedule s);

    // Delete
    boolean delete(Integer id);

    // Read
    Optional<StudentSchedule> findById(Integer id);

    // Lấy lịch của 1 học sinh trong khoảng ngày [from..to]
    List<StudentSchedule> findByStudentAndDateRange(Integer studentUserId, LocalDate from, LocalDate to);

    // Lấy lịch 1 học sinh theo 1 ngày
    List<StudentSchedule> findByStudentAndDate(Integer studentUserId, LocalDate date);

    // Kiểm tra trùng giờ trong 1 ngày (bỏ qua 1 id nếu đang edit)
    boolean existsOverlap(Integer studentId,
                          LocalDate studyDate,
                          LocalTime startTime,
                          LocalTime endTime,
                          Integer ignoreId);
    List<Integer> findStudentIdsByDate(LocalDate date);

}
