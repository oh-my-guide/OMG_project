package com.example.omg_project.domain.user.controller;

import com.example.omg_project.domain.reviewpost.service.ReviewPostService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.service.UserService;
import com.example.omg_project.global.jwt.util.JwtTokenizer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ReviewPostService reviewPostService;
    private final JwtTokenizer jwtTokenizer;

    /**
     * 모든 사용자 조회
     */
    @GetMapping("/admin/userboard")
    public String adminPageAllUserForm(Model model, HttpServletRequest request) {

        model.addAttribute("users", userService.findAllUsers());
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if(accessToken != null){
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
        }
        return "user/admin-all-user";
    }

    @GetMapping("/admin/reviewboard")
    public String adminPageAllReviewForm(Model model, HttpServletRequest request) {
        model.addAttribute("reviews", reviewPostService.findAllReviewPost());
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if(accessToken != null){
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
        }
        return "user/admin-all-review";
    }

    @GetMapping("/faq")
    public String adminPageAllFaqForm(Model model, HttpServletRequest request) {
        model.addAttribute("faqs", reviewPostService.findAllReviewPost());
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if(accessToken != null){
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
        }
        return "/main/faq";
    }
}
