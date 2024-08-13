package com.example.omg_project.domain.user.controller;

import com.example.omg_project.domain.user.dto.request.UserSignUpDto;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.service.UserService;
import com.example.omg_project.global.jwt.util.JwtTokenizer;
import jakarta.servlet.http.HttpServletRequest;

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

    private final UserService userService;
    private final JwtTokenizer jwtTokenizer;

    /**
     * 메인 홈 페이지
     */
    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if(accessToken != null){
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
        }
        return "/user/home";
    }

    /**
     * 회원가입
     */
    @GetMapping("/signup")
    public String signup() {
        return "/main/signupform";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("user") UserSignUpDto userSignUpDto,
                         Model model) {
        try {
            userService.signUp(userSignUpDto);
            model.addAttribute("success", "성공적으로 회원가입 되었습니다.");
            return "redirect:/signin";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "/main/signupform";
        }
    }

    /**
     * 로그인
     */
    @GetMapping("/signin")
    public String showLoginForm() {
        return "/main/loginform";
    }
}