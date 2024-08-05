package com.example.omg_project.domain.joinpost.entity;

import com.example.omg_project.domain.reviewpost.entity.ReviewPostReply;
import com.example.omg_project.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "join_post_comments")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinPostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "join_post_id", nullable = false)
    private JoinPost joinPost;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "joinPostComment", cascade = CascadeType.ALL)
    private List<JoinPostReply> joinPostReplies;

    // JPA의 영속성 컨텍스트 덕분에 entity 객체의 값만 변경하면 자동으로 변경사항 반영함 -> repository.update 를 쓰지 않아도 됨!
    public void updateContent(String content) {
        this.content = content;
    }
}
