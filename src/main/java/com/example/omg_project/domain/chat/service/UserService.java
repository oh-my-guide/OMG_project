package com.example.omg_project.domain.chat.service;

import com.example.omg_project.domain.user.entity.User;

public interface UserService {
    User findByUsername(String username);

    User registerNewUser(User user);
}
