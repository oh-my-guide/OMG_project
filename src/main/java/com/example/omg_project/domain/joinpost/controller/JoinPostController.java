package com.example.omg_project.domain.joinpost.controller;

import com.example.omg_project.domain.joinpost.dto.JoinPostDto;
import com.example.omg_project.domain.joinpost.service.JoinPostService;
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
@RequestMapping("/joinPost")
public class JoinPostController {
    private final JoinPostService joinPostService;
    private final UserService userService;

    @GetMapping("/createPost")
    public String createPost() {
        return "join/createpost";
    }

    @GetMapping("/")
    public String listPosts() {
        return "join/listposts";
    }

    @GetMapping("/{postId}")
    public String viewPost(@PathVariable Long postId, Authentication authentication, Model model) {
        // 게시글 가져오기
        JoinPostDto.Response post = joinPostService.findJoinPostById(postId);
        model.addAttribute("post", post);

        // 로그인한 사용자 정보 가져오기
        Optional<User> byUsername = userService.findByUsername(authentication.getName());
        byUsername.ifPresent(user -> model.addAttribute("user", user));

        return "join/viewpost";
    }

    @GetMapping("/{postId}/updatePost")
    public String updatePost(@PathVariable Long postId, Model model) {
        model.addAttribute("post", joinPostService.findJoinPostById(postId));
        return "join/updatepost";
    }

}