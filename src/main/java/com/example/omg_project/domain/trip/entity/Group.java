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
@Table (name = "groups")
@Getter
@Setter
@NoArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
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

    @ManyToMany
    private Set<User> users = new HashSet<>();
}