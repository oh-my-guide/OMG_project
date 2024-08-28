package com.example.omg_project.domain.user.controller;

import com.example.omg_project.domain.reviewpost.service.ReviewPostService;
import com.example.omg_project.domain.user.entity.AdminNotice;
import com.example.omg_project.domain.user.repository.AdminNoticeRepository;
import com.example.omg_project.domain.user.service.AdminNoticeService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.service.UserService;
import com.example.omg_project.global.jwt.util.JwtTokenizer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ReviewPostService reviewPostService;
    private final JwtTokenizer jwtTokenizer;
    private final AdminNoticeService omgPosterService;
    private final AdminNoticeRepository omgPosterRepository;

    /**
     * 모든 사용자 조회
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/userboard")
    public String adminPageAllUserForm(Model model, HttpServletRequest request) {

        model.addAttribute("users", userService.findAllUsers());
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if (accessToken != null) {
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
        }
        return "user/admin-all-user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/reviewboard")
    public String adminPageAllReviewForm(Model model, HttpServletRequest request) {
        model.addAttribute("reviews", reviewPostService.findAllReviewPost());
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if (accessToken != null) {
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
        }
        return "user/admin-all-review";
    }

    /**
     * 회원 정지 API
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{userId}")
    @ResponseBody
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 게시글 정지 API
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/posts/{postId}")
    @ResponseBody
    public ResponseEntity<Void> deleteReviewPost(@PathVariable("postId") Long postId) {
        try {
            reviewPostService.deleteReviewPost(postId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 공지사항 작성 폼
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/create/notice")
    public String createNoticesForm(Model model, HttpServletRequest request) {

        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if(accessToken != null){
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
            return "/user/admin-notice";
        }
        return "redirect:/";
    }

    /**
     * 공지사항 작성 API
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/notices")
    @ResponseBody
    public ResponseEntity<Void> createNotice(@RequestBody AdminNotice notice) {
        try {
            String title = notice.getTitle();
            String content = notice.getContent();
            if (title == null || title.isEmpty() || content == null || content.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            omgPosterService.saveNotice(title, content);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 모든 공지글 확인 폼
     */
    @GetMapping("/notices")
    public String listNoticeForm(Model model, HttpServletRequest request) {
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if(accessToken != null){
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
            List<AdminNotice> notices = omgPosterService.getAllNotices();
            model.addAttribute("notices", notices);
            return "/user/admin-notice-view";
        }
        return "redirect:/";
    }

    /**
     * 공지글 상세보기 폼
     * @param id 공지글 아이디
     * @param model
     * @param request
     * @return
     */
    @GetMapping("/notices/{id}")
    public String viewNoticeDetail(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if(accessToken != null){
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
            AdminNotice notice = omgPosterRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid notice Id:" + id));
            model.addAttribute("notice", notice);
            return "/user/admin-noticeDetail";
        }
        return "redirect:/";
    }
}