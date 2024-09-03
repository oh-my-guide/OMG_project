package com.example.omg_project.domain.joinpost.controller;

import com.example.omg_project.domain.joinpost.dto.JoinPostDto;
import com.example.omg_project.domain.joinpost.service.JoinPostService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.service.UserService;
import com.example.omg_project.global.jwt.util.JwtTokenizer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/joinPost")
public class JoinPostController {
    private final JoinPostService joinPostService;
    private final UserService userService;
    private final JwtTokenizer jwtTokenizer;

    @GetMapping("/createPost")
    public String createPost(HttpServletRequest request, Model model) {
        // 로그인한 사용자 정보 가져오기
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if(accessToken != null){
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
        }
        return "join/createpost";
    }

    @GetMapping("/")
    public String listPosts(HttpServletRequest request, Model model) {
        // 로그인한 사용자 정보 가져오기
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if(accessToken != null){
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
        }
        return "join/listposts";
    }

    @GetMapping("/{postId}")
    public String viewPost(@PathVariable Long postId, HttpServletRequest request, Model model) {
        // 게시글 가져오기
        JoinPostDto.Response post = joinPostService.findJoinPostById(postId);
        model.addAttribute("post", post);

        // 로그인한 사용자 정보 가져오기
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if(accessToken != null){
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);

            // 조회수 증가 (본인 게시글이 아닌 경우에만)
            if (user != null && !user.getId().equals(post.getUserId())) {
                joinPostService.incrementViews(postId);
                post = joinPostService.findJoinPostById(postId);
                model.addAttribute("post", post);
            }
        }

        return "join/viewpost";
    }

    @GetMapping("/{postId}/updatePost")
    public String updatePost(@PathVariable Long postId, HttpServletRequest request, Model model) {
        model.addAttribute("post", joinPostService.findJoinPostById(postId));
        // 로그인한 사용자 정보 가져오기
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if(accessToken != null){
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
        }
        return "join/updatepost";
    }

}