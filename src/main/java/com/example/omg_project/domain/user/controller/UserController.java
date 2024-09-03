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

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtTokenizer jwtTokenizer;
    private final ImageService imageService;

    /**
     * 로그인 회원의 마이페이지
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
            e.printStackTrace();
            return "redirect:/signin";
        }
    }

    /**
     * OAuth2 로그인 회원 추가 정보 기입 페이지 이동
     */
    @GetMapping("/oauthPage")
    public String addOauth2Form(Model model, HttpServletRequest request) {
        try {
            String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
            if(accessToken != null) {
                String username = jwtTokenizer.getUsernameFromToken(accessToken);
                User user = userService.findByUsername(username).orElse(null);
                if (user.getGender().equals("default")) {
                    model.addAttribute("user", user);
                    return "user/oauth2page";
                }
            }return "redirect:/my";

        } catch (RuntimeException e) {
            e.printStackTrace();
            return "redirect:/signin";
        }
    }

    @PostMapping("/oauthPage")
    @ResponseBody
    public ResponseEntity<String> addOauth2(@RequestBody Oauth2LoginRequest oauth2LoginDto, HttpServletRequest request) {
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if (accessToken != null) {
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            userService.updateOauth2(username, oauth2LoginDto);
            return ResponseEntity.ok("정보가 성공적으로 저장되었습니다.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("정보를 저장하는 중 오류가 발생했습니다.");
    }

    /**
     * 회원 정보 수정 페이지 이동
     */
    @GetMapping("/my/profile")
    public String userEditForm(Model model, HttpServletRequest request) {

        try {
            String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
            if(accessToken != null) {
                String username = jwtTokenizer.getUsernameFromToken(accessToken);
                User user = userService.findByUsername(username).orElse(null);
                model.addAttribute("user", user);
                return "user/mypageEdit";
            }
            return "redirect:/signin";
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "redirect:/signin";
        }
    }

    /**
     * 회원 정보 수정 처리 API
     * userEditDto = JSON 데이터, profileImage = file 이므로 file은 JSON에 포함될 수 없고 JSON, file을 포함한 다중 요청 처리하기 위해 @RequestPart 사용
     */
    @PutMapping("/api/users/profile")
    @ResponseBody
    public ResponseEntity<String> updateUserProfile(HttpServletRequest request,
                                                    @RequestPart(value = "userEditDto") UserEditRequest userEditDto,
                                                    @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if (accessToken != null) {
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            try {
                // 유저 정보 업데이트
                userService.updateUser(username, userEditDto);

                // 프로필 이미지 파일을 선택했을 경우, 프로필 이미지 업데이트
                if (profileImage != null && !profileImage.isEmpty()) {
                    String imageUrl = imageService.upload(profileImage, "UserProfileImage");
                    userService.updateProfileImage(username, imageUrl);
                }
                return ResponseEntity.ok("회원정보가 수정되었습니다.");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원정보 수정 중 오류가 발생했습니다.");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * 비밀번호 재발급 페이지 이동
     */
    @GetMapping("/users/reset-user-password")
    public String showResetPasswordForm() {
        return "user/findPassword";
    }

    /**
     * 비밀번호 재설정 페이지 이동
     */
    @GetMapping("/my/change-password")
    public String changePasswordForm(HttpServletRequest request, Model model) {
        model.addAttribute("userPasswordChangeRequest", new UserPasswordChangeRequest());

        try {
            String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
            if(accessToken != null) {
                String username = jwtTokenizer.getUsernameFromToken(accessToken);
                User user = userService.findByUsername(username).orElse(null);
                model.addAttribute("user", user);
                return "user/change-Password";
            }
            return "redirect:/my";
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "redirect:/signin";
        }
    }

    /**
     * 비밀번호 재설정 API
     */
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