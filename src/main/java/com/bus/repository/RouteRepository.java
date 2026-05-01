package com.bus.repository;

import com.bus.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findBySourceIgnoreCaseAndDestinationIgnoreCase(String source, String destination);
}
