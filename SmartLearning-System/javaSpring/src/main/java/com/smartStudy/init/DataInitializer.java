package com.smartStudy.init;

import com.smartStudy.pojo.User;
import com.smartStudy.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserService userService;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Đảm bảo chỉ chạy một lần duy nhất
        if (alreadySetup) {
            return;
        }

        String adminEmail = "admin@gmail.com";
        // Giả sử userService.getUsers(Map<String,Object>) trả về list Users
        boolean exists = userService.getUsers(Map.of("email", adminEmail))
                .stream()
                .findAny()
                .isPresent();

        if (!exists) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword("123456");
            admin.setName("Administrator");
            admin.setRole("admin");
            userService.addUpdateUser(admin);
            System.out.println("==> [DataInitializer] Tạo user admin: " + adminEmail);
        } else {
            System.out.println("==> [DataInitializer] User admin đã tồn tại, không tạo lại.");
        }

        alreadySetup = true;
    }
}
