package com.example.omg_project.domain.user.controller;

import com.example.omg_project.domain.user.dto.UserEditDto;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
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

    private final UserServiceImpl userServiceimpl;

    /**
     * 일반 로그인 회원의 정보
     */
    @GetMapping("/my")
    public String index(Model model, Authentication authentication) {

        String username = authentication.getName();
        Optional<User> userOptional = userServiceimpl.findByUsername(username);

        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "user/mypage";
        }
        return "redirect:/signin";
    }

    /**
     * OAuth2 로그인 회원의 정보
     */
    @GetMapping("/oauthPage")
    public String info(Model model, Authentication authentication) {

        String username = authentication.getName();
        Optional<User> userOptional = userServiceimpl.findByUsername(username);

        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "user/oauthPage";
        }
        return "redirect:/signin";
    }

    /**
     * 회원 정보 수정
     */
    @GetMapping("/my/profile")
    public String userEditForm(Model model, Authentication authentication) {

        String username = authentication.getName();

        Optional<User> userOptional = userServiceimpl.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);
            model.addAttribute("username", username);

            model.addAttribute("isSocialUser", user.getProvider() != null && !user.getProvider().isEmpty());
            model.addAttribute("isRegularUser", user.getProvider() == null || user.getProvider().isEmpty());

            return "/user/mypageEdit";
        }
        return "redirect:/loginform";
    }

    /**
     * 회원 정보 수정 처리
     */
    @PostMapping("/my/profile")
    public String editUser(Authentication authentication, @ModelAttribute UserEditDto userEditDto, RedirectAttributes redirectAttributes) {

        String username = authentication.getName();

        try {
            Optional<User> updatedUser = userServiceimpl.updateUser(username, userEditDto);
            if (updatedUser.isPresent()) {
                redirectAttributes.addFlashAttribute("msg", "회원정보가 수정되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("msg", "회원정보 수정에 실패했습니다.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg", "회원정보 수정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/my/profile";
    }
}