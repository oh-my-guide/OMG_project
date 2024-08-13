package com.example.omg_project.domain.user.controller;

import com.example.omg_project.domain.user.service.UserService;
import com.mysql.cj.protocol.x.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    /**
     * 모든 사용자 조회
     */
    @GetMapping("/admin/userboard")
    public String adminPageAllUserForm(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "user/admin-all-user";
    }
}
