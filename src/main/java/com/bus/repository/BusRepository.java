package com.bus.repository;

import com.bus.model.Bus;
import com.bus.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BusRepository extends JpaRepository<Bus, Long> {
    List<Bus> findByRoute(Route route);
    List<Bus> findByRouteIn(List<Route> routes);
}
