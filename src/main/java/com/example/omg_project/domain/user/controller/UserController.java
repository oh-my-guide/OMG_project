package com.example.omg_project.domain.user.controller;

import com.example.omg_project.domain.user.dto.request.Oauth2LoginRequest;
import com.example.omg_project.domain.user.dto.request.UserEditRequest;
import com.example.omg_project.domain.user.dto.request.UserPasswordChangeRequest;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.service.UserService;
import com.example.omg_project.global.image.service.ImageService;
import com.example.omg_project.global.jwt.util.JwtTokenizer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtTokenizer jwtTokenizer;
    private final ImageService imageService;

    /**
     * 모든 로그인 회원의 마이페이지
     */
    @GetMapping("/my")
    public String myPage(Model model, HttpServletRequest request) {
        try {
            String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
            if(accessToken != null){
                String username = jwtTokenizer.getUsernameFromToken(accessToken);
                User user = userService.findByUsername(username).orElse(null);
                model.addAttribute("user", user);
                return "user/mypage";
            }
            return "redirect:/";

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
        if(accessToken != null){
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
            return "/user/mypageEdit";
        }
        return "redirect:/signin";
    }

    /**
     * 회원 정보 수정 처리
     */
    @PutMapping("/api/users/profile")
    @ResponseBody
    public ResponseEntity<String> updateUserProfile(HttpServletRequest request, @RequestBody UserEditRequest userEditDto, @RequestParam("profileImage") MultipartFile profileImage) {
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if (accessToken != null) {
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            try {
                userService.updateUser(username, userEditDto);
                // 프로필 이미지 파일을 선택했을 경우
                if (profileImage != null && !profileImage.isEmpty()) {
                    String imageUrl = imageService.upload(profileImage);
                    userService.updateProfileImage(username, imageUrl);
                }
                return ResponseEntity.ok("회원정보가 수정되었습니다.");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원정보 수정 중 오류가 발생했습니다.");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
        if(accessToken != null){
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);

            return "user/change-Password";
        }
        return "redirect:/my";

    }

    @PutMapping("/api/users/change-password")
    @ResponseBody
    public ResponseEntity<String> changePassword(HttpServletRequest request,
                                                 @RequestBody UserPasswordChangeRequest userPasswordChangeRequest) {
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if (accessToken != null) {
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            boolean success = userService.changePassword(username, userPasswordChangeRequest);
            if (success) {
                return ResponseEntity.ok("비밀번호가 변경되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("현재 비밀번호가 올바르지 않습니다.");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}