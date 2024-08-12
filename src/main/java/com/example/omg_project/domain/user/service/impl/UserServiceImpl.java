package com.example.omg_project.domain.user.service.impl;

import com.example.omg_project.domain.role.entity.Role;
import com.example.omg_project.domain.role.repository.RoleRepository;
import com.example.omg_project.domain.user.dto.request.UserEditDto;
import com.example.omg_project.domain.user.dto.request.UserSignUpDto;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import com.example.omg_project.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    /**
     * 회원가입 메서드
     */
    @Override
    @Transactional
    public void signUp(UserSignUpDto userSignUpDto) {
        if (!userSignUpDto.getPassword().equals(userSignUpDto.getPasswordCheck())) {
            throw new RuntimeException("비밀번호가 다릅니다.");
        }
        if (userRepository.existsByUsername(userSignUpDto.getUsername())) {
            throw new RuntimeException("이메일이 존재합니다.");
        }
        if (userRepository.existsByUsernick(userSignUpDto.getUsernick())) {
            throw new RuntimeException("닉네임이 존재합니다.");
        }

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("User 역할이 없습니다."));

        User user = new User();
        user.setRoles(Collections.singleton(role));     // 단일 역할 지정 --> 모든 사용자는 ROLE_USER
        user.setUsername(userSignUpDto.getUsername());  // 이메일
        user.setPassword(passwordEncoder.encode(userSignUpDto.getPassword())); // 비밀번호 암호화
        user.setUsernick(userSignUpDto.getUsernick());  // 닉네임
        user.setName(userSignUpDto.getName());          // 본명
        user.setRegistrationDate(LocalDateTime.now());  // 가입일
        user.setBirthdate(userSignUpDto.getBirthdate()); // 생일
        user.setGender(userSignUpDto.getGender());       // 성별
        user.setPhoneNumber(userSignUpDto.getPhoneNumber()); // 연락처

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
    public void deleteUser(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
        } else {
            throw new RuntimeException("삭제할 사용자가 존재하지 않습니다.");
        }
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
    public Optional<User> updateUser(String username, UserEditDto userEditDto) {

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.error("사용자 없습니다. :: {}", username);
            return Optional.empty();
        }

        User user = userOptional.get();

        user.setUsernick(userEditDto.getUsernick());
        user.setBirthdate(userEditDto.getBirthdate());
        user.setGender(userEditDto.getGender());
        user.setPhoneNumber(userEditDto.getPhoneNumber());

        User updatedUser = userRepository.save(user);
        return Optional.of(updatedUser);
    }

    /**
     * 인증 정보 가져오기
     */
    @Override
    public Optional<User> getAuthenticatedUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();
            String username = null;

            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                username = (String) principal;
            }
            return userRepository.findByUsername(username);
        }catch (Exception e) {
            log.info("로그인 사용자를 찾지 못했습니다.", e);
            return Optional.empty();
        }
    }
}