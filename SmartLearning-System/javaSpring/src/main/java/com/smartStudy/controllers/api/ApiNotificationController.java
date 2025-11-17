package com.smartStudy.controllers.api;
import com.smartStudy.dto.NotificationDTO;
import com.smartStudy.pojo.Notification;
import com.smartStudy.pojo.Student;
import com.smartStudy.pojo.Teacher;
import com.smartStudy.services.NotifcationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiNotificationController {
    @Autowired
    private  NotifcationService notifcationService;

    public ApiNotificationController(NotifcationService notifcationService) {
        this.notifcationService = notifcationService;
    }

    // GET /api/notifications?teacherId=..&studentId=..&limit=..&beforeId=..&type=..&isReaded=..
    @GetMapping(value = "/notifications")
    public ResponseEntity<?> notificationList(@RequestParam Map<String, String> params) {
        String teacherId = params.get("teacherId");
        String studentId = params.get("studentId");
        if ((teacherId == null || teacherId.isBlank()) &&
                (studentId == null || studentId.isBlank())) {
            return ResponseEntity.badRequest().body(
                    Map.of("ok", false, "error", "Missing teacherId or studentId")
            );
        }

        var entities = notifcationService.getNotifications(params);
        return ResponseEntity.ok(NotificationDTO.fromEntities(entities));
    }

    // POST /api/notifications
    // Body JSON (ít nhất phải có 1 trong 2: studentId hoặc teacherId)
    // {
    //   "studentId": 4,
    //   "teacherId": 2,
    //   "type": "SUBMISSION",
    //   "title": "Thông báo chấm điểm nộp bài",
    //   "message": "Giỏi lắm em",
    //   "isReaded": false
    // }
    @PostMapping(value = "/notifications", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createNotification(@RequestBody CreateNotificationRequest req) {
        if (req.getStudentId() == null && req.getTeacherId() == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("ok", false, "error", "studentId or teacherId is required")
            );
        }
        if (req.getTitle() == null || req.getTitle().isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of("ok", false, "error", "title is required")
            );
        }

        Notification n = new Notification();
        if (req.getStudentId() != null) {
            Student st = new Student();
            st.setUserId(req.getStudentId()); // map theo entity của bạn
            n.setStudentId(st);
        }
        if (req.getTeacherId() != null) {
            Teacher t = new Teacher();
            t.setUserId(req.getTeacherId());
            n.setTeacherId(t);
        }
        n.setType(req.getType() == null ? "SUBMISSION" : req.getType());
        n.setTitle(req.getTitle());
        n.setMessage(req.getMessage());
        n.setIsReaded(Boolean.TRUE.equals(req.getIsReaded()));
        n.setSentAt(new java.util.Date()); // hoặc để service gán

        Notification saved = notifcationService.saveOrUpdate(n);
        return ResponseEntity.ok(NotificationDTO.fromEntity(saved));
    }
    @PutMapping(value = "/notifications/read-all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> markAllRead(
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) Integer teacherId,
            @RequestParam(required = false) String type
    ) {
        if (studentId == null && teacherId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("ok", false, "error", "studentId or teacherId is required"));
        }
        int updated = notifcationService.markAllRead(studentId, teacherId, type);
        return ResponseEntity.ok(Map.of("ok", true, "updated", updated));
    }

    // --- request body class dành cho POST ---
    public static class CreateNotificationRequest {
        private Integer studentId;
        private Integer teacherId;
        private String type;
        private String title;
        private String message;
        private Boolean isReaded;

        public Integer getStudentId() { return studentId; }
        public void setStudentId(Integer studentId) { this.studentId = studentId; }
        public Integer getTeacherId() { return teacherId; }
        public void setTeacherId(Integer teacherId) { this.teacherId = teacherId; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Boolean getIsReaded() { return isReaded; }
        public void setIsReaded(Boolean isReaded) { this.isReaded = isReaded; }
    }
}
