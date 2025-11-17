package com.smartStudy.controllers.api;

import com.cloudinary.Cloudinary;
import com.smartStudy.pojo.User;
import com.smartStudy.services.UserService;
import com.smartStudy.untils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class ApiUserController {
    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> userlist(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.userService.getUsers(params), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserId(@PathVariable(value = "userId") int id) {
        return new ResponseEntity<>(this.userService.getUserById(id), HttpStatus.OK);
    }

    @PostMapping(path = "/users",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> create(@RequestParam Map<String, String> params, @RequestParam(value = "avatar") MultipartFile avatar) {
        return new ResponseEntity<>(this.userService.addUserClient(params, avatar), HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable(value = "userId") int id) {
        this.userService.deleteUser(id);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User u) {
        if (this.userService.authenticate(u.getEmail(), u.getPassword())) {
            try {
                String token = JwtUtils.generateToken(u.getEmail());
                return ResponseEntity.ok().body(Collections.singletonMap("token", token));
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Lỗi khi tạo JWT");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai thông tin đăng nhập");
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {
        System.out.println("Received payload: " + payload);
        try {
            String idToken = payload.get("idToken");
            System.out.println("Extracted idToken: " + idToken);
            if (idToken == null || idToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing Google ID token");
            }
            String token = userService.authenticateGoogle(idToken);
            System.out.println("Returning JWT: " + token);
            return ResponseEntity.ok().body(Collections.singletonMap("token", token));
        } catch (Exception e) {
            System.out.println("Google login error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Google login failed: " + e.getMessage());
        }
    }

    @PutMapping(path = "/users/{userId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(
            @PathVariable("userId") int id,
            @RequestParam Map<String, String> params,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {

        User existingUser = this.userService.getUserById(id);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + id + " not found");
        }

        // Tạo user mới để map params vào
        User u = new User();
        u.setId(id);
        u.setName(params.get("name"));
        u.setEmail(params.get("email"));
        u.setPassword(params.get("password")); // Service sẽ tự giữ hoặc encode
        u.setRole(params.get("role"));
        u.setFile(avatar);

        User updatedUser = this.userService.addUpdateUser(u);
        return ResponseEntity.ok(updatedUser);
    }

    @RequestMapping("/auth/user")
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<?> getProfile(Authentication user) {
        String email = user.getName();
        User u = userService.getUserByMail(email);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng!");
        }
        return ResponseEntity.ok(u);
    }

    // ------------------ FORGOT PASSWORD: GỬI OTP ------------------
    @PatchMapping(path = "/auth/forgot-password", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email là bắt buộc"));
        }

        // Luôn trả về message chung để không lộ email có tồn tại hay không
        this.userService.issueResetOtp(email);

        return ResponseEntity.ok(
                Map.of("message", "Nếu tài khoản tồn tại, OTP đã được gửi tới email của bạn")
        );
    }

    // ------------------ RESET PASSWORD BẰNG OTP ------------------
    @PatchMapping(path = "/auth/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String otp = req.get("otp");
        String newPassword = req.get("newPassword");
        String confirmNewPassword = req.get("confirmNewPassword");

        if (email == null || otp == null || newPassword == null || confirmNewPassword == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Thiếu thông tin bắt buộc"));
        }

        if (!newPassword.equals(confirmNewPassword)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Mật khẩu xác nhận không khớp"));
        }

        boolean ok = this.userService.resetPasswordWithOtp(email, otp, newPassword);
        if (!ok) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "OTP không hợp lệ hoặc đã hết hạn"));
        }

        return ResponseEntity.ok(
                Map.of("message", "Đặt lại mật khẩu thành công")
        );
    }
}
