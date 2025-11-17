package com.smartStudy.schedule;

import com.smartStudy.services.EmailService;
import com.smartStudy.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Component
public class StudyReminderScheduler {

    private static final ZoneId VN = ZoneId.of("Asia/Ho_Chi_Minh");

    @Autowired
    private final EmailService emailService;
    @Autowired
    private final ScheduleService scheduleService;

    public StudyReminderScheduler(EmailService emailService, ScheduleService scheduleService) {
        this.emailService = emailService;
        this.scheduleService = scheduleService;
    }

    /**
     * Chạy mỗi ngày lúc 7:00 sáng (giờ VN).
     */
    @Scheduled(cron = "0 14 16 * * *", zone = "Asia/Ho_Chi_Minh")
    public void runDailyIfHasSchedules() {
        LocalDate today = LocalDate.now(VN);
        List<Integer> studentIds = scheduleService.findStudentIdsByDate(today);
        if (studentIds == null || studentIds.isEmpty()) return;
        for (Integer sid : studentIds) {
            try {
                emailService.remindStudy(sid, today);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
