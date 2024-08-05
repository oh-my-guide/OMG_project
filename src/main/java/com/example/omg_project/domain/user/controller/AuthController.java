package com.example.omg_project.domain.user.controller;

import com.example.omg_project.domain.user.dto.UserSignUpDto;
import com.example.omg_project.domain.user.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 로그인, 회원가입, 비로그인 페이지
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserServiceImpl userServiceimpl;

    // 비로그인, 메인
    @GetMapping("/")
    public String home() {
        return "/user/home";
    }

    // 회원가입
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new UserSignUpDto());
        return "/main/signupform";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("user") UserSignUpDto userSignUpDto,
                         Model model) {
        try {
            userServiceimpl.signUp(userSignUpDto);
            model.addAttribute("success", "성공적으로 회원가입 되었습니다.");
            return "redirect:/signin";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "/main/signupform";
        }
    }

    // 로그인
    @GetMapping("/signin")
    public String showLoginForm() {
        return "/main/loginform";
    }
}
