package com.bus.repository;

import com.bus.model.Bus;
import com.bus.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByBus(Bus bus);
    List<Seat> findByBusId(Long busId);
}
