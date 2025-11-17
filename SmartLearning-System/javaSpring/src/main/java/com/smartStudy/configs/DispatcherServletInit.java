/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbsp/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.configs;
import io.github.cdimascio.dotenv.Dotenv;
import com.smartStudy.filters.JwtFilter;
import com.smartStudy.init.DataInitializer;
import jakarta.servlet.Filter;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * @author DUNG
 */
public class DispatcherServletInit extends AbstractAnnotationConfigDispatcherServletInitializer {

    static {
        try {
            String envPath = ".env";
            if (!Files.exists(Paths.get(envPath))) {
                System.err.println("File .env không tồn tại tại: " + Paths.get(envPath).toAbsolutePath());
            } else {
                System.out.println("Tìm thấy file .env tại: " + Paths.get(envPath).toAbsolutePath());
                Dotenv dotenv = Dotenv.configure()
                        .directory("./") // Thư mục gốc
                        .ignoreIfMissing()
                        .load();
                dotenv.entries().forEach(entry -> {
                    System.setProperty(entry.getKey(), entry.getValue());
                    System.out.println("Đã tải biến môi trường: " + entry.getKey() + "=" + entry.getValue());
                });
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải file .env: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{
                ThymeleafConfig.class,
                HibernateConfig.class,
                SpringSecurityConfig.class,
                EmailConfig.class,
                DataInitializer.class,
                FirebaseAdminConfig.class
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{
                WebAppContextConfigs.class,
                DataInitializer.class
        };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        String tmp = System.getProperty("java.io.tmpdir"); // dùng thư mục tạm của hệ thống
        long maxFileSize = 10L * 1024 * 1024; // 10 MB / file
        long maxRequestSize = 20L * 1024 * 1024; // 20 MB / request (hoặc cao hơn)
        int fileSizeThreshold = 0;

        registration.setMultipartConfig(
                new MultipartConfigElement(tmp, maxFileSize, maxRequestSize, fileSizeThreshold)
        );
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[]{new JwtFilter()}; // Filter sẽ áp dụng cho mọi request
    }

}