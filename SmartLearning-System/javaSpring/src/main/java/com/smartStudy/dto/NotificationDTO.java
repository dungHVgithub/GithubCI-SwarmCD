package com.smartStudy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartStudy.pojo.Notification;
import com.smartStudy.pojo.Student;
import com.smartStudy.pojo.Teacher;
import com.smartStudy.pojo.User;

import java.util.ArrayList;
import java.util.List;

public class NotificationDTO {

    private Integer id;
    private String type;       // "SUBMISSION", "DEADLINE", ...
    private String title;
    private String message;

    private boolean readed;    // JSON key vẫn là "isReaded"
    private Long sentAt;       // epoch millis

    private UserSimpleDTO student; // chỉ id, name, email
    private UserSimpleDTO teacher; // chỉ id, name, email

    // ---------- getters / setters ----------
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @JsonProperty("isReaded")
    public boolean isReaded() { return readed; }
    @JsonProperty("isReaded")
    public void setReaded(boolean readed) { this.readed = readed; }

    public Long getSentAt() { return sentAt; }
    public void setSentAt(Long sentAt) { this.sentAt = sentAt; }

    public UserSimpleDTO getStudent() { return student; }
    public void setStudent(UserSimpleDTO student) { this.student = student; }

    public UserSimpleDTO getTeacher() { return teacher; }
    public void setTeacher(UserSimpleDTO teacher) { this.teacher = teacher; }

    // ---------- mapping ----------
    public static NotificationDTO fromEntity(Notification n) {
        if (n == null) return null;

        NotificationDTO dto = new NotificationDTO();
        dto.setId(n.getId());
        dto.setType(n.getType());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setReaded(Boolean.TRUE.equals(n.getIsReaded()));
        // Giả định Notification.sentAt là java.util.Date
        dto.setSentAt(n.getSentAt() != null ? n.getSentAt().getTime() : null);

        // student -> UserSimpleDTO
        dto.setStudent(toUserSimple(n.getStudentId()));
        // teacher -> UserSimpleDTO
        dto.setTeacher(toUserSimple(n.getTeacherId()));

        return dto;
    }

    public static List<NotificationDTO> fromEntities(List<Notification> list) {
        List<NotificationDTO> out = new ArrayList<>();
        if (list == null) return out;
        for (Notification n : list) out.add(fromEntity(n));
        return out;
    }

    // ---------- helpers ----------
    private static UserSimpleDTO toUserSimple(Student st) {
        if (st == null) return null;
        UserSimpleDTO u = new UserSimpleDTO();
        // Student PK thường là userId; nếu entity của bạn dùng field khác, đổi lại cho khớp
        u.setId(st.getUserId());
        if (st.getUser() != null) {
            u.setName(st.getUser().getName());
            u.setEmail(st.getUser().getEmail());
        }
        return u;
    }

    private static UserSimpleDTO toUserSimple(Teacher t) {
        if (t == null) return null;
        UserSimpleDTO u = new UserSimpleDTO();
        u.setId(t.getUserId());
        if (t.getUser() != null) {
            u.setName(t.getUser().getName());
            u.setEmail(t.getUser().getEmail());
        }
        return u;
    }
}

