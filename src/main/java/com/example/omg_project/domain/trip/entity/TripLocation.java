package com.example.omg_project.domain.trip.entity;

import com.example.omg_project.domain.reviewpost.entity.PlaceReview;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "trip_locations")
@Getter
@Setter
@NoArgsConstructor
public class TripLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_date_id", nullable = false)
    private TripDate tripDate;

    @Column(name = "place_name", length = 100, nullable = false)
    private String placeName;

    @Column(name = "latitude", precision = 9, scale = 6, nullable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6, nullable = false)
    private BigDecimal longitude;

    @OneToMany(mappedBy = "tripLocation", cascade = CascadeType.ALL)
    private List<PlaceReview> placeReviews;
}
