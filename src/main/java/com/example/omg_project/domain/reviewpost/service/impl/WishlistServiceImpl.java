package com.example.omg_project.domain.reviewpost.service.impl;

import com.example.omg_project.domain.reviewpost.dto.WishlistDto;
import com.example.omg_project.domain.reviewpost.entity.ReviewPost;
import com.example.omg_project.domain.reviewpost.entity.Wishlist;
import com.example.omg_project.domain.reviewpost.repository.ReviewPostRepository;
import com.example.omg_project.domain.reviewpost.repository.WishlistRepository;
import com.example.omg_project.domain.reviewpost.service.WishlistService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import com.example.omg_project.global.exception.CustomException;
import com.example.omg_project.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ReviewPostRepository reviewPostRepository;

    /**
     * 찜하기 토글
     */
    @Override
    public void toggleWishlist(WishlistDto.Request request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
        ReviewPost reviewPost = reviewPostRepository.findById(request.getReviewPostId()).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND_EXCEPTION));

        Wishlist existingWishlist = wishlistRepository.findByUserIdAndReviewPostId(user.getId(), reviewPost.getId());

        if (existingWishlist != null) {
            wishlistRepository.delete(existingWishlist);
        } else {
            Wishlist wishlist = request.toEntity(user, reviewPost);
            wishlistRepository.save(wishlist);
        }
    }

    /**
     * 찜하기 상태 확인
     */
    @Override
    public boolean isPostInWishlist(Long userId, Long postId) {
        return wishlistRepository.existsByUserIdAndReviewPostId(userId, postId);
    }

    /**
     * 찜한 게시글 전체 조회
     */
    @Override
    public List<WishlistDto.Response> getWishlistByUserId(Long userId) {
        List<Wishlist> wishlists = wishlistRepository.findAllByUserId(userId);
        return wishlists.stream().map(WishlistDto.Response::fromEntity).collect(Collectors.toList());
    }
}
