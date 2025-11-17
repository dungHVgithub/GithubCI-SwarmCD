package com.smartStudy.services.impl;

import com.smartStudy.pojo.StudentSchedule;
import com.smartStudy.pojo.User;
import com.smartStudy.services.EmailService;
import com.smartStudy.services.ScheduleService;
import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class EmailServiceImpl implements EmailService {
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String systemFrom;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String feUrl;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendGradedNotice(String studentEmail,
                                 String teacherName,
                                 String teacherEmail,
                                 String exerciseTitle,
                                 Long submissionId,
                                 String viewUrl,
                                 Integer grade,
                                 String feedback) {
        try {
            var msg = mailSender.createMimeMessage();
            var h = new MimeMessageHelper(msg, "UTF-8");

            // From = tài khoản hệ thống, tên hiển thị = Teacher via SmartStudy
            h.setFrom(new InternetAddress(systemFrom, (teacherName == null || teacherName.isBlank()
                    ? "Teacher" : teacherName) + " via SmartStudy"));

            // Student nhận mail; Reply-To về teacher
            if (teacherEmail != null && !teacherEmail.isBlank()) h.setReplyTo(teacherEmail);
            h.setTo(studentEmail);

            String subject = "[SmartStudy] Bài của bạn đã được chấm: " + exerciseTitle;
            h.setSubject(subject);

            String link = (viewUrl == null || viewUrl.isBlank())
                    ? feUrl + "/student/submissions/" + (submissionId == null ? "" : submissionId)
                    : viewUrl;

            String gradeLine = (grade != null) ? "<li>Điểm: <b>" + grade + "</b></li>" : "";
            String feedbackBlock = (feedback != null && !feedback.isBlank())
                    ? "<p><b>Nhận xét:</b><br/>" + feedback + "</p>" : "";

            String html = """
            <p>Xin chào,</p>
            <p>Thầy/Cô <b>%s</b> đã <b>chấm bài</b> <b>%s</b> của bạn.</p>
            <ul>
              <li>Mã bài nộp: <b>%s</b></li>
              %s
            </ul>
            %s
            <p>Trân trọng,<br/>SmartStudy</p>
        """.formatted(
                    (teacherName == null || teacherName.isBlank()) ? "Giáo viên" : teacherName,
                    (exerciseTitle == null || exerciseTitle.isBlank()) ? "Bài tập" : exerciseTitle,
                    (submissionId == null ? "" : submissionId.toString()),
                    gradeLine,
                    feedbackBlock,
                    link, link
            );

            h.setText(html, true);
            mailSender.send(msg);
        } catch (Exception ignored) {
            // TODO: log cảnh báo nếu cần
        }
    }

    @Override
    public void sendNoticeSubmit(String teacherEmail,
                                 String teacherName,
                                 String studentName,
                                 String exerciseTitle,
                                 Long submissionId,
                                 String viewUrl) {
        if (teacherEmail == null || teacherEmail.isBlank()) return;

        try {
            var msg = mailSender.createMimeMessage();
            var h = new MimeMessageHelper(msg, "UTF-8");

            // From: tài khoản hệ thống; tên hiển thị: SmartStudy Bot (hoặc Teacher Name nếu bạn thích)
            h.setFrom(new InternetAddress(systemFrom, "SmartStudy"));
            h.setTo(teacherEmail);

            String safeTeacher = (teacherName == null || teacherName.isBlank()) ? "Thầy/Cô" : teacherName;
            String safeStudent = (studentName == null || studentName.isBlank()) ? "Sinh viên" : studentName;
            String safeTitle   = (exerciseTitle == null || exerciseTitle.isBlank()) ? "Bài tập" : exerciseTitle;

            // Link xem bài nộp cho giáo viên:
            String link = (viewUrl == null || viewUrl.isBlank())
                    ? feUrl + "/teacher/submissions/" + (submissionId == null ? "" : submissionId)
                    : viewUrl;

            String subject = "[SmartStudy] " + safeStudent + " đã nộp bài: " + safeTitle;
            h.setSubject(subject);

            String html = """
            <p>Xin chào %s,</p>
            <p><b>%s</b> vừa <b>nộp bài</b> <b>%s</b>.</p>
            <ul>
              <li>Mã bài nộp: <b>%s</b></li>
            </ul>
            <p>Trân trọng,<br/>SmartStudy</p>
        """.formatted(
                    safeTeacher,
                    safeStudent,
                    safeTitle,
                    (submissionId == null ? "" : submissionId.toString()),
                    link, link
            );

            h.setText(html, true);
            mailSender.send(msg);
        } catch (Exception ex) {
            // TODO: ghi log nếu cần
            ex.printStackTrace();
        }
    }

    @Override
    public void sendPlainText(String to, String subject, String content) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(systemFrom);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(content);
        mailSender.send(msg);
    }

    @Override
    public void sendOtpEmail(String to, String otp, long ttlMinutes) {
        String subject = "[StudySmart] Mã OTP đặt lại mật khẩu";
        String body =
                "Xin chào,\n\n" +
                        "Mã OTP đặt lại mật khẩu của bạn là: " + otp + "\n" +
                        "OTP có hiệu lực trong " + ttlMinutes + " phút.\n\n" +
                        "Nếu không phải bạn yêu cầu, vui lòng bỏ qua email này.";
        sendPlainText(to, subject, body);
    }

    @Override
    public void remindStudy(LocalDate date) {
        List<Integer> studentIds = scheduleService.findStudentIdsByDate(date);
        if (studentIds == null || studentIds.isEmpty()) return;

        for (Integer sid : studentIds) {
            try {
                remindStudy(sid, date); // tái dùng hàm “1 student”
            } catch (Exception ex) {
                // log lỗi nhưng không làm gián đoạn các student khác
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void remindStudy(Integer studentId, LocalDate date) {
        if (studentId == null || date == null) return;

        List<StudentSchedule> schedules = scheduleService.findByStudentAndDate(studentId, date);
        if (schedules == null || schedules.isEmpty()) return;

        // Lấy email + tên từ 1 record (cùng 1 student)
        StudentSchedule any = schedules.get(0);
        String to = null;
        String studentName = null;
        if (any.getStudentId() != null && any.getStudentId().getUserId() != null) {
            User u = any.getStudentId().getUser();
            to = u.getEmail();
            studentName = u.getName();
        }
        if (to == null || to.isBlank()) return;

        // Sort theo startTime cho đẹp
        schedules.sort(Comparator.comparing(StudentSchedule::getStartTime,
                Comparator.nullsLast(Comparator.naturalOrder())));

        String subject = "[SmartStudy] Nhắc lịch học ngày " + date;
        String body = buildPlainBodyForStudent(date, schedules, studentName);

        sendPlainText(to, subject, body);
    }

    private String buildPlainBodyForStudent(LocalDate date,
                                            List<StudentSchedule> list,
                                            String studentName) {
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        StringBuilder sb = new StringBuilder();

        if (studentName != null && !studentName.isBlank()) {
            sb.append("Chào ").append(studentName).append(",\n\n");
        } else {
            sb.append("Chào bạn,\n\n");
        }
        sb.append("Hôm nay (").append(date).append(") bạn có ")
                .append(list.size()).append(" lịch học.\n\nChi tiết:\n");

        for (StudentSchedule sc : list) {
            String start = sc.getStartTime() != null ? sc.getStartTime().format(timeFmt) : "--:--";
            String end   = sc.getEndTime()   != null ? sc.getEndTime().format(timeFmt)   : "--:--";
            String subjectTitle = (sc.getSubjectId() != null && sc.getSubjectId().getTitle() != null)
                    ? sc.getSubjectId().getTitle() : "Môn học";
            String note = (sc.getNote() != null && !sc.getNote().isBlank())
                    ? sc.getNote() : "(Không có ghi chú)";

            sb.append("Thời gian: ").append(start).append(" – ").append(end).append("\n")
                    .append("Môn học: ").append(subjectTitle).append("\n")
                    .append("Ghi chú: ").append(note).append("\n")
                    .append("-------------------------\n");
        }

        sb.append("\nChúc bạn học tốt!\nSmartStudy");
        return sb.toString();
    }
}

