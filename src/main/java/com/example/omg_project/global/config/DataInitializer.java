package com.example.omg_project.global.config;

import com.example.omg_project.domain.role.entity.Role;
import com.example.omg_project.domain.role.repository.RoleRepository;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 관리자 설정 로직
 */
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Value("${omgAdmin.username}") String adminUsername;
    @Value("${omgAdmin.name}") String adminName;
    @Value("${omgAdmin.password}") String adminPassword;
    @Value("${omgAdmin.gender}") String adminGender;
    @Value("${omgAdmin.phoneNumber}") String adminPhoneNumber;
    @Value("${omgAdmin.adminNick}") String adminNick;
    String adminBirthday = LocalDate.now().toString();

    @Bean
    public CommandLineRunner initRolesAndAdminUser() {
        return args -> {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("ROLE_ADMIN");
                        return roleRepository.save(newRole);
                    });

            // 관리자 계정이 없으면 생성
            if (userRepository.findByUsername(adminUsername).isEmpty()) {
                User adminUser = new User();
                adminUser.setUsername(adminUsername);
                adminUser.setPassword(passwordEncoder.encode(adminPassword));
                adminUser.setUsernick(adminNick);
                adminUser.setPhoneNumber(adminPhoneNumber);
                adminUser.setName(adminName);
                adminUser.setBirthdate(LocalDate.parse(adminBirthday));
                adminUser.setGender(adminGender);

                adminUser.getRoles().add(adminRole);

                userRepository.save(adminUser);
            }
        };
    }
}