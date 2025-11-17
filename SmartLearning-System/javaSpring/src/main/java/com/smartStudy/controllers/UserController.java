package com.smartStudy.controllers;

import com.smartStudy.pojo.User;
import com.smartStudy.repositories.UserRepository;
import com.smartStudy.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private static final int PAGE_SIZE = 5;

    @GetMapping("/login")
    public String loginView() {
        return "login";
    }

    @GetMapping("/users")
    public String listUsers(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam Map<String, String> params,
            Model model
    ) {
        if (page == null) return "redirect:/users?page=1";
        page = parseInt(params.get("page"), 1);
        page = Math.max(1, page);
        params.put("page", String.valueOf(page));
        List<User> users = userService.getUsers(params);
        long total = userRepository.countUsers();
        long totalPages = (total / PAGE_SIZE) + 1;
        model.addAttribute("users", users);
        model.addAttribute("page", page);
        model.addAttribute("size", PAGE_SIZE);
        model.addAttribute("total", total);
        model.addAttribute("totalPages", totalPages);

        return "users";
    }


    // Helper parse int an toàn
    private int parseInt(String v, int defVal) {
        try {
            return (v == null || v.isBlank()) ? defVal : Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return defVal;
        }
    }


    @GetMapping("/users/add")
    public String addUserView(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Arrays.asList("ADMIN", "STUDENT", "TEACHER")); // Danh sách vai trò tĩnh
        return "editUser";
    }

    @PostMapping("/users/add")
    public String addUser(@ModelAttribute("user") User user, Model model) {
        // Nếu là update (user đã có id)
        if (user.getId() != null) {
            User currentUser = userService.getUserById(user.getId());
            // Nếu email đã thay đổi
            if (!user.getEmail().equals(currentUser.getEmail())) {
                // Kiểm tra email mới đã tồn tại ở user khác chưa
                if (userService.exitsByEmail(user.getEmail())) {
                    model.addAttribute("emailError", "Email already exists, please choose another email!");
                    model.addAttribute("user", user);
                    return "editUser";
                }
            }
            // Email giữ nguyên hoặc không trùng -> update bình thường
            userService.addUpdateUser(user);
            return "redirect:/users";
        } else { // Thêm mới
            if (userService.exitsByEmail(user.getEmail())) {
                model.addAttribute("emailError", "Email đã tồn tại, hãy nhập email mới");
                model.addAttribute("user", user);
                return "editUser";
            }
            userService.addUpdateUser(user);
            return "redirect:/users";
        }
    }


    @GetMapping("/users/{userId}")
    public String updateUserView(Model model, @PathVariable(value = "userId") int id) {
        model.addAttribute("user", this.userService.getUserById(id));
        model.addAttribute("roles", Arrays.asList("ADMIN", "STUDENT", "TEACHER")); // Danh sách vai trò tĩnh
        return "editUser";
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable(value = "userId") int id) {
        this.userService.deleteUser(id);
    }

}
