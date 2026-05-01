package com.bus.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "buses")
@Data
@NoArgsConstructor
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    private String busName;
    private String busNumber;
    private double price; // base price per seat
    private int totalSeats;

    // Comma-separated: AC, WIFI, CHARGING
    private String amenities;

    private String departureTime;
    private String arrivalTime;
}
