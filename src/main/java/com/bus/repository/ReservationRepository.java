package com.bus.repository;

import com.bus.model.Reservation;
import com.bus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByBusIdAndTravelDateAndStatus(Long busId, LocalDate travelDate, String status);

    // Date range filter for a specific user
    @Query("SELECT r FROM Reservation r WHERE r.user = :user AND r.travelDate BETWEEN :startDate AND :endDate")
    List<Reservation> findByUserAndTravelDateBetween(@Param("user") User user,
                                                      @Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);

    // Date range filter for all reservations (admin)
    @Query("SELECT r FROM Reservation r WHERE r.travelDate BETWEEN :startDate AND :endDate")
    List<Reservation> findByTravelDateBetween(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(r.totalFare) FROM Reservation r WHERE r.status = 'CONFIRMED'")
    Double getTotalRevenue();

    @Query("SELECT r FROM Reservation r WHERE r.bus.id = :busId AND r.travelDate = :date AND r.status = 'CONFIRMED'")
    List<Reservation> findConfirmedByBusAndDate(@Param("busId") Long busId, @Param("date") LocalDate date);
}
