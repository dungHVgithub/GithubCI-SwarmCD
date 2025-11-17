package com.smartStudy.services;

import com.smartStudy.pojo.StudentSchedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ScheduleService {

    // Tạo mới 1 lịch học cho học sinh (dùng id để gắn quan hệ)
    StudentSchedule create(Integer studentId,
                           Integer subjectId,
                           LocalDate studyDate,
                           LocalTime startTime,
                           LocalTime endTime,
                           String note);

    // Cập nhật đầy đủ (sửa môn/ngày/giờ/ghi chú...)
    StudentSchedule update(Integer id,
                           Integer subjectId,
                           LocalDate studyDate,
                           LocalTime startTime,
                           LocalTime endTime,
                           String note);

    // Cập nhật nhanh giờ/ngày (phục vụ drag & drop / resize)
    StudentSchedule updateTime(Integer id,
                               LocalDate studyDate,
                               LocalTime startTime,
                               LocalTime endTime);

    // Xoá
    boolean delete(Integer id);

    // Lấy lịch theo id (throw nếu không có)
    StudentSchedule getRequired(Integer id);

    // Lấy lịch học của 1 học sinh trong khoảng ngày
    List<StudentSchedule> findByStudentAndDateRange(Integer studentUserId,
                                                    LocalDate from,
                                                    LocalDate to);

    // Lấy lịch 1 ngày
    List<StudentSchedule> findByStudentAndDate(Integer studentId, LocalDate date);
    List<Integer> findStudentIdsByDate(LocalDate date);
}
