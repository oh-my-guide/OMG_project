package com.example.omg_project.domain.reviewpost.controller;

import com.example.omg_project.domain.reviewpost.dto.ReviewPostDto;
import com.example.omg_project.domain.reviewpost.service.ReviewPostService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviewPost")
public class ReviewPostController {
    private final ReviewPostService reviewPostService;
    private final UserService userService;

    @GetMapping("/createPost")
    public String createPost(Authentication authentication, Model model) {
        // 로그인한 사용자의 userId 가져오기
        Optional<User> byUsername = userService.findByUsername(authentication.getName());
        byUsername.ifPresent(user -> model.addAttribute("userId", user.getId()));
        return "review/createpost";
    }

    @GetMapping("/")
    public String listPosts() {
        return "review/listposts";
    }

    @GetMapping("/{postId}")
    public String viewPost(@PathVariable Long postId, Authentication authentication, Model model) {
        // 게시글 가져오기
        ReviewPostDto.Response post = reviewPostService.findReviewPostById(postId);
        model.addAttribute("post", post);

        // 로그인한 사용자 정보 가져오기
        Optional<User> byUsername = userService.findByUsername(authentication.getName());
        byUsername.ifPresent(user -> model.addAttribute("user", user));

        return "review/viewpost";
    }

    @GetMapping("/{postId}/updatePost")
    public String updatePost(@PathVariable Long postId, Model model) {
        model.addAttribute("post", reviewPostService.findReviewPostById(postId));
        return "review/updatepost";
    }

}