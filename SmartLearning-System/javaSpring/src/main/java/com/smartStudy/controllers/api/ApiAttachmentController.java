package com.smartStudy.controllers.api;

import com.smartStudy.pojo.ChapterAttachment;
import com.smartStudy.services.AttachmentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiAttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @PostMapping(value = "/chapters/{chapterId}/attachments", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> upload(
            @PathVariable("chapterId") Integer chapterId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", required = false) String type) { // NEW

        ChapterAttachment a = attachmentService.upload(chapterId, file, type);

        Map<String, Object> body = new HashMap<>();
        body.put("id", a.getId());
        body.put("chapterId", a.getChapterId().getId());
        body.put("filename", a.getFilename());
        body.put("type", a.getType());         // CONTENT | SUMMARY
        body.put("extension", a.getExtension());// pdf | doc | docx
        body.put("url", a.getFilepath());
        body.put("uploadedAt", a.getUploadedAt());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/chapters/{chapterId}/attachments")
    public ResponseEntity<List<Map<String, Object>>> list(@PathVariable("chapterId") Integer chapterId) {
        List<Map<String, Object>> out = attachmentService.listByChapter(chapterId).stream().map(a -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", a.getId());
            m.put("filename", a.getFilename());
            m.put("type", a.getType());
            m.put("extension", a.getExtension());
            m.put("url", a.getFilepath());
            m.put("uploadedAt", a.getUploadedAt());
            return m;
        }).toList();
        return ResponseEntity.ok(out);
    }

    // Map MIME theo đuôi
    private String mimeOf(String ext) {
        if (ext == null) return "application/octet-stream";
        return switch (ext.toLowerCase()) {
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default -> "application/octet-stream";
        };
    }

    // Đặt Content-Disposition với tên UTF-8 (hỗ trợ tên có dấu)
    private void setDisposition(HttpServletResponse resp, String dispo, String filename) {
        try {
            String asciiName = filename.replaceAll("[\\r\\n\"]", "_");
            String utf8 = java.net.URLEncoder.encode(filename, java.nio.charset.StandardCharsets.UTF_8).replace("+", "%20");
            resp.setHeader("Content-Disposition",
                    dispo + "; filename=\"" + asciiName + "\"; filename*=UTF-8''" + utf8);
        } catch (Exception ignore) {
        }
    }

    // XEM: PDF hiển thị inline; Word thì tự động tải (browser không render doc/docx)
    @GetMapping("/attachments/{id}/open")
    public void open(@PathVariable("id") Integer id, HttpServletResponse resp) throws IOException {
        var a = attachmentService.getAttachment(id);
        String ext = a.getExtension();
        String ct = mimeOf(ext);

        // PDF: inline; Word: attachment
        boolean isPdf = "pdf".equalsIgnoreCase(ext);
        resp.setContentType(ct);
        setDisposition(resp, isPdf ? "inline" : "attachment",
                a.getFilename() != null ? a.getFilename() : ("file." + ext));

        try (var in = new java.net.URL(a.getFilepath()).openStream();
             var out = resp.getOutputStream()) {
            in.transferTo(out);
            out.flush();
        }
    }

    // TẢI VỀ: luôn tải về đúng đuôi và tên file gốc
    @GetMapping("/attachments/{id}/download")
    public void download(@PathVariable("id") Integer id, HttpServletResponse resp) throws IOException {
        var a = attachmentService.getAttachment(id);
        String ext = a.getExtension();
        String ct = mimeOf(ext);

        resp.setContentType(ct);
        setDisposition(resp, "attachment",
                a.getFilename() != null ? a.getFilename() : ("file." + ext));

        try (var in = new java.net.URL(a.getFilepath()).openStream();
             var out = resp.getOutputStream()) {
            in.transferTo(out);
            out.flush();
        }
    }



    @DeleteMapping("/attachments/{id}")
    public ResponseEntity<Void> delete(@PathVariable(value = "id") Integer id) {
        attachmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
