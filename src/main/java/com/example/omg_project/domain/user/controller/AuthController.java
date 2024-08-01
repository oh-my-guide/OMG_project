package com.example.omg_project.domain.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 로그인, 비로그인 페이지
 */
@Controller
public class AuthController {

    @GetMapping("/")
    public String home() {
        return "/user/home";
    }

    @GetMapping("/signup")
    public String signup() {
        return "/main/signupform";
    }

    @GetMapping("/signin")
    public String signin() {
        return "/main/loginform";
    }
}
