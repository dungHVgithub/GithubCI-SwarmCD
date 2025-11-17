// com.smartStudy.services.EmailService.java
package com.smartStudy.services;

import java.time.LocalDate;

public interface EmailService {
    // thêm method mới
    void sendGradedNotice(String studentEmail,
                          String teacherName,
                          String teacherEmail,
                          String exerciseTitle,
                          Long submissionId,
                          String viewUrl,
                          Integer grade,
                          String feedback);
    void sendNoticeSubmit(String teacherEmail, String teacherName, String studentName, String exerciseTitle,
                          Long submissionId, String viewUrl);
    void sendPlainText(String to, String subject, String content);
    void sendOtpEmail(String to, String otp, long ttlMinutes);
    void remindStudy(LocalDate date);
    void remindStudy(Integer studentId,LocalDate date);


}
