package com.bus.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    private LocalDate travelDate;
    private LocalDateTime bookingDate;

    private String boardingPoint;
    private String droppingPoint;

    // Comma-separated seat numbers booked
    private String seatsBooked;

    private double totalFare;

    private String status; // "CONFIRMED" or "CANCELLED"

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<Passenger> passengers;
}
