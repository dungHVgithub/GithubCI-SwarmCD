package com.smartStudy.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.smartStudy.pojo.Chapter;
import com.smartStudy.pojo.ChapterAttachment;
import com.smartStudy.repositories.AttachmentRepository;
import com.smartStudy.services.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    @Autowired
    private  Cloudinary cloudinary;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    private static final Set<String> ALLOWED = Set.of("pdf", "doc", "docx");

    private Session cur() {
        SessionFactory ss = factoryBean.getObject();
        return ss.getCurrentSession();
    }


    @Override
    @Transactional
    public ChapterAttachment upload(Integer chapterId, MultipartFile file, String type) {
        if (file == null || file.isEmpty()) throw new RuntimeException("File rỗng");

        String original = Objects.requireNonNull(file.getOriginalFilename());
        String ext = getExt(original);
        if (!ALLOWED.contains(ext)) throw new RuntimeException("Chỉ cho phép pdf/doc/docx");

        Chapter chapter = cur().get(Chapter.class, chapterId);
        if (chapter == null) throw new RuntimeException("Không tìm thấy Chapter id=" + chapterId);

        // chuẩn hoá type
        String t = (type == null ? "" : type.trim().toUpperCase());
        if (!t.equals("CONTENT") && !t.equals("SUMMARY")) t = "CONTENT";

        try {
            String resourceType = "raw"; // cả PDF và Word
            String publicId = buildPublicId(chapterId, original);

            var res = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", resourceType,
                            "public_id", publicId,
                            "folder", "chapters/" + chapterId
                    )
            );

            ChapterAttachment att = new ChapterAttachment();
            att.setType(t);               // <<< set theo dropdown FE
            att.setExtension(ext);
            att.setFilename(original);
            att.setFilepath((String) res.get("secure_url"));
            att.setUploadedAt(new Date());
            att.setChapterId(chapter);
            return attachmentRepository.save(att);
        } catch (IOException e) {
            throw new RuntimeException("Upload thất bại", e);
        }
    }



    @Override
    public List<ChapterAttachment> listByChapter(Integer chapterId) {
        return attachmentRepository.findByChapterId(chapterId);
    }

    @Override
    public ChapterAttachment getAttachment(Integer id) {
        ChapterAttachment a = attachmentRepository.findById(id);
        if (a == null) throw new RuntimeException("Không tìm thấy attachment id=" + id);
        return a;
    }

    @Override
    public void delete(Integer id) {
        ChapterAttachment att = getAttachment(id);

        String resourceType = "pdf".equalsIgnoreCase(att.getType()) ? "image" : "raw";
        String publicId = extractPublicIdFromUrl(att.getFilepath(), resourceType);

        if (publicId != null) {
            try {
                cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", resourceType));
            } catch (Exception ignore) {
            }
        }
        attachmentRepository.delete(att);
    }

    private String getExt(String name) {
        int i = name.lastIndexOf('.');
        return i > -1 ? name.substring(i + 1).toLowerCase() : "";
    }

    private String buildPublicId(Integer chapterId, String original) {
        String base = original.replaceAll("\\.[^.]+$", ""); // bỏ đuôi
        base = base.replaceAll("[^a-zA-Z0-9-_]+", "_");
        return "chapters/" + chapterId + "/" + base + "-" + System.currentTimeMillis();
    }

    /**
     * URL mẫu:
     * image: https://res.cloudinary.com/<cloud>/image/upload/v17123456/chapters/1/name-...pdf
     * raw:   https://res.cloudinary.com/<cloud>/raw/upload/v17123456/chapters/1/name-...docx
     * Trả về public_id: chapters/1/name-...
     */
    private String extractPublicIdFromUrl(String url, String resourceType) {
        try {
            String marker = "/" + resourceType + "/upload/";
            int idx = url.indexOf(marker);
            if (idx < 0) return null;
            String afterUpload = url.substring(idx + marker.length()); // v.../chapters/1/name-...pdf
            if (afterUpload.startsWith("v")) {
                int slash = afterUpload.indexOf('/');
                if (slash > 0) afterUpload = afterUpload.substring(slash + 1);
            }
            int dot = afterUpload.lastIndexOf('.');
            if (dot > 0) afterUpload = afterUpload.substring(0, dot);
            return afterUpload;
        } catch (Exception e) {
            return null;
        }
    }
}
