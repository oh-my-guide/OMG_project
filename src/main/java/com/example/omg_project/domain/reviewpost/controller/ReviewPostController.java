package com.example.omg_project.domain.reviewpost.controller;

import com.example.omg_project.domain.reviewpost.dto.PlaceReviewDto;
import com.example.omg_project.domain.reviewpost.dto.ReviewPostDto;
import com.example.omg_project.domain.reviewpost.service.PlaceReviewService;
import com.example.omg_project.domain.reviewpost.service.ReviewPostService;
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

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviewPost")
public class ReviewPostController {
    private final ReviewPostService reviewPostService;
    private final UserService userService;
    private final PlaceReviewService placeReviewService;
    private final JwtTokenizer jwtTokenizer;

    /**
     * 게시글 작성 화면으로 이동
     */
    @GetMapping("/createPost")
    public String createPost(HttpServletRequest request, Model model) {
        // 로그인한 사용자 정보 가져오기
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if(accessToken != null){
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
        }
        return "review/createpost";
    }

    /**
     * 목록 화면으로 이동
     */
    @GetMapping("/")
    public String listPosts(HttpServletRequest request, Model model) {
        // 로그인한 사용자 정보 가져오기
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if(accessToken != null){
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
        }
        return "review/listposts";
    }

    /**
     * 게시글 상세 화면으로 이동
     */
    @GetMapping("/{postId}")
    public String viewPost(@PathVariable Long postId, HttpServletRequest request, Model model) {
        // 게시글 가져오기
        ReviewPostDto.Response post = reviewPostService.findReviewPostById(postId);
        model.addAttribute("post", post);

        // 장소별 리뷰 가져오기
        List<PlaceReviewDto.Response> placeReviews = placeReviewService.findByReviewPostId(postId);
        model.addAttribute("placeReviews", placeReviews);

        // 로그인한 사용자 정보 가져오기
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if(accessToken != null){
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);

            // 조회수 증가 (본인 게시글이 아닌 경우에만)
            if (user != null && !user.getId().equals(post.getUserId())) {
                reviewPostService.incrementViews(postId);
                post = reviewPostService.findReviewPostById(postId);
                model.addAttribute("post", post);
            }
        }

        return "review/viewpost";
    }

    /**
     * 게시글 수정 화면으로 이동
     */
    @GetMapping("/{postId}/updatePost")
    public String updatePost(@PathVariable Long postId, HttpServletRequest request, Model model) {
        // 게시글 가져오기
        model.addAttribute("post", reviewPostService.findReviewPostById(postId));

        // 로그인한 사용자 정보 가져오기
        String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);
        if(accessToken != null){
            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
        }
        return "review/updatepost";
    }

}