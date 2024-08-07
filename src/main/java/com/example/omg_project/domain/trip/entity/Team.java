package com.example.omg_project.domain.trip.entity;

import com.example.omg_project.domain.chat.entity.ChatRoom;
import com.example.omg_project.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @OneToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "invite_code", nullable = false, length = 50)
    private String inviteCode;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User leaderId;

    @ManyToMany(mappedBy = "teams", fetch = FetchType.EAGER)
    private Set<User> users = new HashSet<>();
}