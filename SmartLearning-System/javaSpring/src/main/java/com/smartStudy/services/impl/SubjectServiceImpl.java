/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.smartStudy.pojo.Student;
import com.smartStudy.services.UserService;
import com.smartStudy.statictis.SubjectStat;
import com.smartStudy.pojo.Subject;
import com.smartStudy.repositories.SubjectRepository;
import com.smartStudy.services.SubjectService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author AN515-57
 */
@Service
public class SubjectServiceImpl  implements SubjectService{
    @Autowired
    private SubjectRepository subjectRepo;

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public List<Subject> getSubjects(Map<String, String> params) {return this.subjectRepo.getSubjects(params);
    }

    @Override
    public Subject getSubjectById(int id) {
        return this.subjectRepo.getSubjectById(id);
    }
    @Override
    public Subject addOrUpdate(Subject s) {
        // Lấy đối tượng hiện có nếu là cập nhật
        Subject existingSubject = null;
        if (s.getId() != null) {
            existingSubject= this.subjectRepo.getSubjectById(s.getId());
        }

        //Xử lý trường ảnh
        if (s.getFile() != null && !s.getFile().isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(s.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                s.setImage(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(SubjectService.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (existingSubject != null) {
            // Giữ nguyên avatar hiện có nếu không tải lên file mới
            s.setImage(existingSubject.getImage());
        }


        // Xử lý createdAt và updatedAt
        LocalDateTime now = LocalDateTime.now();
        Date currentDate = Date.from(now.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());

        if (s.getId() == null) {
            // Khi add mới: đặt cả createdAt và updatedAt giống nhau
            s.setCreatedAt(currentDate);
            s.setUpdatedAt(currentDate);
        } else {
            // Khi update: chỉ cập nhật updatedAt, giữ nguyên createdAt và birthday
            s.setUpdatedAt(currentDate);
            if (existingSubject != null) {
                s.setCreatedAt(existingSubject.getCreatedAt());
            }
        }
        return this.subjectRepo.addOrUpdate(s);
    }

    @Override
    public void deleteSubject(int id) {
        this.subjectRepo.deleteSubject(id);
    }

    @Override
    public Long quantityAll() {
        return this.subjectRepo.quantityAll();
    }

    @Override
    public List<SubjectStat> countBySubjectInWeek() {
        return this.subjectRepo.countBySubjectInWeek();
    }

    @Override
    public List<SubjectStat> countBySubjectInMonth() {
        return this.subjectRepo.countBySubjectInMonth();
    }

}
