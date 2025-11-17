package com.smartStudy.services;

import com.smartStudy.pojo.ChapterAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {
    ChapterAttachment upload(Integer chapterId, MultipartFile file, String type);
    List<ChapterAttachment> listByChapter (Integer chapterId);
    ChapterAttachment getAttachment(Integer id);
    void delete(Integer id);
}
