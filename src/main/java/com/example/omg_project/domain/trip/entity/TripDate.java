package com.example.omg_project.domain.trip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "trip_dates")
@Getter
@Setter
public class TripDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "trip_date", nullable = false)
    private LocalDate tripDate;

    @Column(name = "day", nullable = false)
    private int day;

}
