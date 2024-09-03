package com.example.omg_project.domain.reviewpost.entity;

import com.example.omg_project.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "review_posts_comments")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "review_post_id", nullable = false)
    private ReviewPost reviewPost;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private boolean secret = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "reviewPostComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewPostReply> reviewPostReplies;

    // 엔티티가 영속화되기 전에 실행되는 메서드
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    // JPA의 영속성 컨텍스트 덕분에 entity 객체의 값만 변경하면 자동으로 변경사항 반영함 -> repository.update 를 쓰지 않아도 됨!
    public void updateContent(String content) {
        this.content = content;
    }
}
