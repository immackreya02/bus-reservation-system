package com.bus.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "passengers")
@Data
@NoArgsConstructor
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private String name;
    private int age;
    private String type; // "ADULT", "CHILD", "INFANT"
    private String seatNumber;
    private String seatType; // "LOWER" or "UPPER"
}
