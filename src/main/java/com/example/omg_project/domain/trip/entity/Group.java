package com.example.omg_project.domain.trip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table (name = "groups")
@Getter
@Setter
@NoArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoom;

    @Column(name = "trip_id", nullable = false)
    private Long trip;

    @Column(name = "invite_code", nullable = false, length = 50)
    private String inviteCode;

    @Column(nullable = false)
    private Long leaderId;
}
