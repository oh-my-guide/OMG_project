package com.example.omg_project.global.oauth2.service;

import com.example.omg_project.domain.role.entity.Role;
import com.example.omg_project.domain.role.repository.RoleRepository;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.repository.UserRepository;
import com.example.omg_project.global.oauth2.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("OAuth2User attributes: " + oAuth2User.getAttributes());
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        switch (registrationId) {
            case "naver":
                log.info("naver 로그인");
                oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
                break;
            case "kakao":
                log.info("kakao 로그인");
                oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
                break;
            case "google":
                log.info("google 로그인");
                oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
                break;
            default:
                log.error("로그인 실패: 지원하지 않는 로그인 제공자입니다. 등록 ID: {}", registrationId);
                throw new IllegalArgumentException("지원하지 않는 로그인 제공자입니다.");
        }

        String provider = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProviderId();
        String name = provider + " " + providerId; // 이렇게 해서 해당 유저가 이미 디비에 있는지 없는지 확인
        Optional<User> userOptional = userRepository.findByUsername(name);

        // "USER" 라는 역할을 OAuth2 로그인 사람에게 다 부여
        String roleName = "ROLE_USER";
        Optional<Role> roleOptional = roleRepository.findByName(roleName);
        Role role;
        if (roleOptional.isEmpty()) {
            role = new Role(roleName);
            role = roleRepository.save(role);
        } else {
            role = roleOptional.get();
        }

        User user;
        // OAuth2 로그인을 한 적 없는 사람
        if (userOptional.isEmpty()) {
            user = User.builder()
                    .name(oAuth2Response.getName())
                    .username(name)
                    .roles(Set.of(role))
                    .providerId(oAuth2Response.getProviderId())
                    .provider(oAuth2Response.getProvider())
                    .password("")
                    // 마이페이지에서 사용자가 직접 설정할 필드들
                    .phoneNumber("")
                    .birthdate(LocalDate.from(LocalDateTime.now()))
                    .gender("")
                    .registrationDate(LocalDateTime.now())
                    .usernick(oAuth2Response.getEmail())
                    .build();
            userRepository.save(user);
        } else { // 이미 OAuth2 로그인을 한 적이 있는 사람
            user = userOptional.get();
            boolean updated = false;

            if (!user.getRoles().contains(role)) {
                user.getRoles().add(role);
                updated = true;
            }

            // 닉네임은 첫 로그인 이후 마이페이지에서만 변경 가능
            if (!user.getUsernick().equals(oAuth2Response.getName()) && user.getUsernick() == null) {
                user.setUsernick(oAuth2Response.getName());
                updated = true;
            }

            if (updated) {
                userRepository.save(user);
            }
        }

        System.out.println("User saved: " + user);

        return new CustomOAuth2User(oAuth2Response, roleName);
    }
}
