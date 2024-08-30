package com.example.omg_project.domain.user.service.impl;

import com.example.omg_project.domain.joinpost.service.JoinPostCommentService;
import com.example.omg_project.domain.joinpost.service.JoinPostReplyService;
import com.example.omg_project.domain.reviewpost.service.ReviewPostCommentService;
import com.example.omg_project.domain.reviewpost.service.ReviewPostReplyService;
import com.example.omg_project.domain.role.entity.Role;
import com.example.omg_project.domain.role.repository.RoleRepository;
import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.trip.repository.TripRepository;
import com.example.omg_project.domain.trip.service.impl.TripServiceImpl;
import com.example.omg_project.domain.user.dto.request.Oauth2LoginRequest;
import com.example.omg_project.domain.user.dto.request.UserEditRequest;
import com.example.omg_project.domain.user.dto.request.UserPasswordChangeRequest;
import com.example.omg_project.domain.user.dto.request.UserSignUpRequest;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import com.example.omg_project.domain.user.service.UserService;
import com.example.omg_project.global.exception.CustomException;
import com.example.omg_project.global.exception.ErrorCode;
import com.example.omg_project.global.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final TripServiceImpl tripServiceImpl;
    private final TripRepository tripRepository;
    private final ReviewPostCommentService reviewPostCommentService;
    private final ReviewPostReplyService reviewPostReplyService;
    private final JoinPostCommentService joinPostCommentService;
    private final JoinPostReplyService joinPostReplyService;
    private final ImageService imageService;

    /**
     * 회원가입 메서드
     */
    @Override
    @Transactional
    public void signUp(UserSignUpRequest userSignUpDto) {

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_NOT_FOUND));

        User user = User.builder()
                .username(userSignUpDto.getUsername())
                .roles(Collections.singleton(role))
                .password(passwordEncoder.encode(userSignUpDto.getPassword()))
                .usernick(userSignUpDto.getUsernick())
                .name(userSignUpDto.getName())
                .birthdate(userSignUpDto.getBirthdate())
                .phoneNumber(userSignUpDto.getPhoneNumber())
                .gender(userSignUpDto.getGender())
                .registrationDate(LocalDateTime.now())
                .status("ACTIVE") // 상태 추가
                .build();

        userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 회원 탈퇴
     */
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String newEmail = generateUnique(user.getUsername());
            String newUserNick = generateUnique(user.getUsernick());

            user.setStatus("DEACTIVATED"); // 상태를 DEACTIVATED로 변경
            user.setUsername(newEmail);
            user.setUsernick(newUserNick);

            List<Trip> trips = tripRepository.findByUserId(userId);
            for (Trip trip : trips) {
                tripServiceImpl.deleteTrip(trip.getId());
            }

            // 작성한 댓글과 대댓글 삭제
            joinPostCommentService.deleteByUserId(user.getId());
            joinPostReplyService.deleteByUserId(user.getId());
            reviewPostCommentService.deleteByUserId(user.getId());
            reviewPostReplyService.deleteByUserId(user.getId());

            userRepository.save(user);

        } else {
            throw new CustomException(ErrorCode.USER_DELETION_ERROR);
        }
    }
    // 탈퇴 시 유니크한 username 부여
    public static String generateUnique(String email) {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        return email + "_" + uniqueId ;
    }

    /**
     * 닉네임 중복 확인
     */
    @Override
    public boolean existsByUsernick(String usernick) {
        return userRepository.existsByUsernick(usernick);
    }

    /**
     * 이메일 중복 확인
     */
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 사용자 페이지 수정
     */
    @Override
    public Optional<User> updateUser(String username, UserEditRequest userEditDto) {

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();
        user.setUsernick(userEditDto.getUsernick());
        user.setPhoneNumber(userEditDto.getPhoneNumber());
        userRepository.save(user);
        return Optional.of(user);
    }

    /**
     * Oauth2 로그인 시 추가 정보 입력
     */
    @Override
    public Optional<User> updateOauth2(String username, Oauth2LoginRequest oauth2LoginDto) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();
        user.setPhoneNumber(oauth2LoginDto.getPhoneNumber());
        user.setGender(oauth2LoginDto.getGender());
        user.setBirthdate(oauth2LoginDto.getBirthdate());

        userRepository.save(user);
        return Optional.of(user);
    }

    /**
     * 모든 사용자 정보 가져오기
     */
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 비밀번호 재설정
     */
    @Override
    public boolean changePassword(String username, UserPasswordChangeRequest userPasswordChangeRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (passwordEncoder.matches(userPasswordChangeRequest.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(userPasswordChangeRequest.getNewPassword()));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void updateProfileImage(String username, String imageUrl) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 기존 프로필 이미지가 존재하면 S3에서 기존 이미지 삭제
            String oldProfileImageUrl = user.getFilepath();
            if (oldProfileImageUrl != null && !oldProfileImageUrl.isEmpty()) {
                imageService.deleteImageFromS3(oldProfileImageUrl);
            }

            // imageUrl에서 파일 이름 추출
            URL url = null;
            try {
                url = new URL(imageUrl);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            // 새로운 이미지 S3에 업로드
            String filename = url.getPath().substring(url.getPath().lastIndexOf("/") + 1);
            user.setFilename(filename);
            user.setFilepath(imageUrl);

            userRepository.save(user);
        }
    }
}