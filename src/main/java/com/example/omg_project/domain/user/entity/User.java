package com.example.omg_project.domain.user.entity;

import com.example.omg_project.domain.reviewpost.entity.Wishlist;
import com.example.omg_project.domain.role.entity.Role;
import com.example.omg_project.domain.trip.entity.Team;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username; // 이메일

    @Column(nullable = false, length = 100)
    private String password; // 비밀번호

    @Column(nullable = false, length = 100)
    private String name; // 본명

    @Column(nullable = false, unique = true, length = 100)
    private String usernick; // 닉네임

    @Column(nullable = false, length = 50)
    private String phoneNumber; // 연락처

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate = LocalDateTime.now(); // 가입일

    @Column(nullable = false)
    private LocalDate birthdate; // 생일

    @Column(nullable = false)
    private String gender; // 성별

    private String filename; // 이미지 파일 이름

    private String filepath; // 이미지 파일 경로

    private String provider; // oauth2 플랫폼

    private String providerId; // 플랫폼 아이디

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "team_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private Set<Team> teams = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Wishlist> wishlists;
}