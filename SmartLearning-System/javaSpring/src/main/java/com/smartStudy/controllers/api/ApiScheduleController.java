package com.smartStudy.controllers.api;

import com.smartStudy.dto.ScheduleDTO;
import com.smartStudy.pojo.StudentSchedule;
import com.smartStudy.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
@Validated
public class ApiScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    /**
     * Lấy danh sách lịch học theo NGÀY đơn lẻ
     */
    @GetMapping("/schedules/student/{studentId}")
    public ResponseEntity<List<ScheduleDTO>> getByDateRange(
            @PathVariable("studentId") Integer studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<StudentSchedule> data = scheduleService.findByStudentAndDateRange(studentId, from, to);
        List<ScheduleDTO> dtos = data.stream()
                .map(ScheduleDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // --- (tuỳ chọn) GET theo ngày đơn lẻ
    @GetMapping("/schedules/student/{studentId}/day")
    public ResponseEntity<List<ScheduleDTO>> getByDate(
            @PathVariable("studentId") Integer studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<StudentSchedule> data = scheduleService.findByStudentAndDate(studentId, date);
        List<ScheduleDTO> dtos = data.stream()
                .map(ScheduleDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }


    /**
     * (Tuỳ chọn) Kiểm tra trùng lịch trước khi tạo/cập nhật
     * GET /api/students/{studentUserId}/schedules/conflict?date=2025-09-01&start=08:00&end=10:00&ignoreId=123
     *
     * Lưu ý: tên method Service dưới đây có thể khác (ví dụ: hasTimeConflict / existsOverlap).
     * Hãy đổi đúng tên theo ScheduleService của bạn.
     */

    /**
     * Tạo mới lịch học
     * POST /api/schedules
     * body: StudentSchedule (JSON) — gồm studentId, subjectId, studyDate (yyyy-MM-dd), startTime (HH:mm), endTime (HH:mm), note
     *
     * Lưu ý: Nếu Service của bạn dùng tên "create" hay "save", hãy chỉnh lại cho khớp.
     */
    // DTO dùng chung cho create/update
    static class ScheduleCreateUpdateReq {
        public Integer studentId;   // create cần
        public Integer subjectId;
            @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
        public java.time.LocalDate studyDate;
        @com.fasterxml.jackson.annotation.JsonFormat(pattern = "HH:mm[:ss]")
        public java.time.LocalTime startTime;
        @com.fasterxml.jackson.annotation.JsonFormat(pattern = "HH:mm[:ss]")
        public java.time.LocalTime endTime;
        public String note;
    }

    // POST /api/schedules
    @PostMapping("/schedules/student/{studentId}")
    public ResponseEntity<StudentSchedule> create(@RequestBody ScheduleCreateUpdateReq req,
                                                  @PathVariable(value = "studentId") Integer studentId) {
        // create(Integer studentId, Integer subjectId, LocalDate studyDate, LocalTime startTime, LocalTime endTime, String note)
        StudentSchedule saved = scheduleService.create(
                studentId,
                req.subjectId,
                req.studyDate,
                req.startTime,
                req.endTime,
                req.note
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/schedules/{id}
    @PutMapping("/schedules/student/{studentId}/{id}")
    public ResponseEntity<StudentSchedule> update(@PathVariable(value = "id") Integer id,
                                                  @PathVariable(value = "studentId") Integer studentId,
                                                  @RequestBody ScheduleCreateUpdateReq req) {
        // update(Integer id, Integer subjectId, LocalDate studyDate, LocalTime startTime, LocalTime endTime, String note)
        StudentSchedule updated = scheduleService.update(
                id,
                req.subjectId,
                req.studyDate,
                req.startTime,
                req.endTime,
                req.note
        );
        return ResponseEntity.ok(updated);
    }


    /**
     * Xoá lịch học
     * DELETE /api/schedules/{id}
     */
    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<Void> delete(@PathVariable(value = "id") Integer id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lấy chi tiết lịch học
     * GET /api/schedules/{id}
     */
    @GetMapping("/schedules/{id}")
    public ResponseEntity<StudentSchedule> getById(@PathVariable(value = "id") Integer id) {
        StudentSchedule s = scheduleService.getRequired(id);
        return ResponseEntity.ok(s);
    }
}
