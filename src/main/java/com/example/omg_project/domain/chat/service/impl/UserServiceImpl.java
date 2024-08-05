package com.example.omg_project.domain.chat.service.impl;

import com.example.omg_project.domain.chat.repository.RoleRepository;
import com.example.omg_project.domain.chat.repository.UserRepository;
import com.example.omg_project.domain.chat.service.UserService;
import com.example.omg_project.domain.role.entity.Role;
import com.example.omg_project.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User registerNewUser(User user) {
        Role userRole = roleRepository.findByName("ROLE_USER");
        user.setRoles(Collections.singleton(userRole));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
