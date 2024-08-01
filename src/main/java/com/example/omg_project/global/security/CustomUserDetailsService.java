package com.example.omg_project.global.security;

import com.example.omg_project.domain.role.entity.Role;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("CustomUserDetailsService -- 로그인 요청이 들어왔습니다.");
        log.info("조회할 아이디 : {} ", username);

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.info("사용자가 없습니다.");
            throw new UsernameNotFoundException("사용자가 존재하지 않습니다." + username);
        }

        User forndUser = userOptional.get();

        return new CustomUserDetails(
                forndUser.getUsername(),
                forndUser.getPassword(),
                forndUser.getRoles());
    }
}
