package com.smartStudy.controllers;
import com.google.firebase.auth.FirebaseAuthException;
import com.smartStudy.pojo.User;
import com.smartStudy.services.FirebaseTokenService;
import com.smartStudy.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/firebase")
@RequiredArgsConstructor
@CrossOrigin // hoặc cấu hình CORS global
public class FirebaseAuthController {
    @Autowired
    private  FirebaseTokenService tokenService;
    @Autowired
    private  UserService userService;

    @GetMapping("/custom-token")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCustomToken(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        }
        String email = auth.getName();
        User u = userService.getUserByMail(email);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy người dùng"));
        }

        String uid = String.valueOf(u.getId()); // uid Firebase = id nội bộ
        String roles = u.getRole();
        try {
            String token = tokenService.createCustomToken(uid, Map.of(
                    "email", u.getEmail(),
                    "roles", roles
            ));
            return ResponseEntity.ok(Map.of("token", token));
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Firebase token error", "message", e.getMessage()));
        }
    }
}