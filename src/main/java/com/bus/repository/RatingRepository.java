package com.bus.repository;

import com.bus.model.Bus;
import com.bus.model.Rating;
import com.bus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByBus(Bus bus);
    Optional<Rating> findByBusAndUser(Bus bus, User user);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.bus.id = :busId")
    Double getAverageRatingByBusId(Long busId);
}
