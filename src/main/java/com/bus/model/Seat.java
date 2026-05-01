package com.bus.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seats")
@Data
@NoArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    private String seatNumber;  // e.g., "1A", "1B"
    private String seatType;    // "LOWER" or "UPPER"

    // status is dynamic per travel date — we use reservations to track
}
