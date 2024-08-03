package com.example.omg_project.domain.user.controller;

import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserServiceImpl userServiceimpl;

    // 일반 로그인 회원의 정보 가져오기
    @GetMapping("/mypage")
    public String index(Model model, Authentication authentication) {

        String username = authentication.getName();
        Optional<User> userOptional = userServiceimpl.findByUsername(username);

        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "user/mypage";
        }
        return "redirect:/signin";
    }
}