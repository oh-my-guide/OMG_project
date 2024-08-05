package com.example.omg_project.domain.joinpost.entity;

import com.example.omg_project.domain.trip.entity.Trip;
import com.example.omg_project.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "join_posts")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "joinPost", cascade = CascadeType.ALL)
    private List<JoinPostComment> comments;

    @OneToMany(mappedBy = "joinPost", cascade = CascadeType.ALL)
    private List<JoinPostLike> likes;

    // JPA의 영속성 컨텍스트 덕분에 entity 객체의 값만 변경하면 자동으로 변경사항 반영함 -> repository.update 를 쓰지 않아도 됨!
    public void updateContent(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
}
