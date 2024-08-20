package com.example.omg_project.domain.user.controller;

import com.example.omg_project.domain.user.dto.request.Oauth2LoginRequest;
import com.example.omg_project.domain.user.dto.request.UserEditRequest;
import com.example.omg_project.domain.user.dto.request.UserPasswordChangeRequest;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtTokenizer jwtTokenizer;

    /**
     * 모든 로그인 회원의 마이페이지
     */
    @GetMapping("/my")
    public String myPage(Model model, HttpServletRequest request) {
        try {
            String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);

            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            Optional<User> userOptional = userService.findByUsername(username);

            User user = userOptional.get();
            model.addAttribute("user", user);

            return "user/mypage";

        } catch (RuntimeException e) {
            log.info("에러 :: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/signin";
        }
    }

    /**
     * OAuth2 로그인 회원 추가 정보 기입
     */
    @GetMapping("/oauthPage")
    public String addOauth2Form(Model model, HttpServletRequest request) {

        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        String username = jwtTokenizer.getUsernameFromToken(accessToken);
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 처음 로그인 한 회원일 경우
            if (user.getGender().equals("default")) {
                model.addAttribute("user", user);
                return "user/oauth2page";
            }
            return "redirect:/my";
        }
        return "redirect:/signin";
    }

    @PostMapping("/oauthPage")
    public String addOauth2(HttpServletRequest request, @ModelAttribute Oauth2LoginRequest oauth2LoginDto, RedirectAttributes redirectAttributes) {

        log.info("Received User: {}", oauth2LoginDto);
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        String username = jwtTokenizer.getUsernameFromToken(accessToken);

        try {
            userService.updateOauth2(username, oauth2LoginDto);
        } catch (Exception e) {
            log.info("추가정보 저장 중 오류 발생 :: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/oauthPage";
    }

    /**
     * 회원 정보 수정
     */
    @GetMapping("/my/profile")
    public String userEditForm(Model model, HttpServletRequest request) {

        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        String username = jwtTokenizer.getUsernameFromToken(accessToken);

        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);

            return "/user/mypageEdit";
        }
        return "redirect:/signin";
    }

    /**
     * 회원 정보 수정 처리
     */
    @PostMapping("/my/profile")
    public String userEdit(HttpServletRequest request, @ModelAttribute UserEditRequest userEditDto, RedirectAttributes redirectAttributes) {

        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        String username = jwtTokenizer.getUsernameFromToken(accessToken);

        try {
            userService.updateUser(username, userEditDto);
        } catch (Exception e) {
            log.info("회원정보 수정 중 오류 발생 :: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/my";
    }

    /**
     * 비밀번호 재발급 페이지 이동
     */
    @GetMapping("/users/reset-user-password")
    public String showResetPasswordForm() {
        return "/user/findPassword";
    }

    /**
     * 비밀번호 재설정
     */
    @GetMapping("/my/change-password")
    public String changePasswordForm(HttpServletRequest request, Model model) {
        model.addAttribute("userPasswordChangeRequest", new UserPasswordChangeRequest());

        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);

        String username = jwtTokenizer.getUsernameFromToken(accessToken);
        Optional<User> userOptional = userService.findByUsername(username);

        User user = userOptional.get();
        model.addAttribute("user", user);
        return "user/change-Password";
    }

    @PostMapping("/my/change-password")
    public String changePassword(HttpServletRequest request,
                                 @ModelAttribute UserPasswordChangeRequest userPasswordChangeRequest,
                                 RedirectAttributes redirectAttributes) {

        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        String username = jwtTokenizer.getUsernameFromToken(accessToken);

        boolean success = userService.changePassword(username, userPasswordChangeRequest);

        if (success) {
            return "redirect:/my";
        } else {
            redirectAttributes.addFlashAttribute("msg", "현재 비밀번호가 올바르지 않습니다.");
            return "redirect:/my/change-password";
        }
    }
}