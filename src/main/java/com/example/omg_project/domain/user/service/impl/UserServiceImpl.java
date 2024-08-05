package com.example.omg_project.domain.user.service.impl;

import com.example.omg_project.domain.role.entity.Role;
import com.example.omg_project.domain.role.repository.RoleRepository;
import com.example.omg_project.domain.user.dto.UserSignUpDto;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import com.example.omg_project.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

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
        user.setRoles(Collections.singleton(role));     // 단일 역할 --> ROLE_USER
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

    @Override
    @Transactional
    public void deleteUser(String email) {
        Optional<User> userOptional = userRepository.findByUsername(email);
        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
        } else {
            throw new RuntimeException("삭제할 사용자가 존재하지 않습니다.");
        }
    }

    @Override
    public boolean existsByUsernick(String usernick) {
        return userRepository.existsByUsernick(usernick);
    }

}
