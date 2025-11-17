package com.smartStudy.repositories;
import  java.util.List;
import com.smartStudy.pojo.ChapterAttachment;


public interface AttachmentRepository {
    ChapterAttachment save(ChapterAttachment a);
    ChapterAttachment findById(Integer id);
    List <ChapterAttachment> findByChapterId(Integer chapterId);
    void delete(ChapterAttachment a);
}
