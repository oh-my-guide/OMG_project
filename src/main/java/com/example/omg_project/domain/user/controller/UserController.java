package com.example.omg_project.domain.user.controller;

import com.example.omg_project.domain.user.dto.UserSignUpDto;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.service.impl.UserServiceImpl;
import com.example.omg_project.global.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserServiceImpl userServiceimpl;

    @GetMapping("/api/users/signup")
    public String signup(Model model) {
        model.addAttribute("user", new UserSignUpDto());
        return "/main/signupform";
    }

    @PostMapping("/api/users/signup")
    public String signup(@ModelAttribute("user") UserSignUpDto userSignUpDto,
                         RedirectAttributes redirectAttributes) {
        try {
            userServiceimpl.signUp(userSignUpDto);
            redirectAttributes.addFlashAttribute("success", " 성공적으로 회원가입 됐습니다.");
            return "redirect:/api/users/login";
        } catch (Exception e) {
            log.error("회원가입 오류 : {} " , e.getMessage());
            redirectAttributes.addFlashAttribute("error", "회원가입에 실패했습니다." + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/api/users/login";
    }

    @GetMapping("/api/users/login")
    public String showLoginForm() {
        return "/main/loginform";
    }

    // 일반 로그인 회원의 정보 가져오기
    @GetMapping("/api/users/info")
    public String index(Model model, Authentication authentication) {

        String username = authentication.getName();
        Optional<User> userOptional = userServiceimpl.findByUsername(username);

        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "user/info";
        }
        return "redirect:/api/users/login";
    }
}