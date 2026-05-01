package com.bus.controller;

import com.bus.model.*;
import com.bus.repository.RatingRepository;
import com.bus.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class BusApiController {

    @Autowired private BusService busService;
    @Autowired private RatingRepository ratingRepository;

    @GetMapping("/search")
    public ResponseEntity<?> searchBuses(@RequestParam String source,
                                          @RequestParam String destination,
                                          @RequestParam String date) {
        List<Bus> buses = busService.searchBuses(source, destination);
        LocalDate travelDate = LocalDate.parse(date);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Bus bus : buses) {
            List<String> booked = busService.getBookedSeats(bus.getId(), travelDate);
            int available = bus.getTotalSeats() - booked.size();
            Double avgRating = busService.getAverageRating(bus.getId());

            Map<String, Object> busData = new LinkedHashMap<>();
            busData.put("id", bus.getId());
            busData.put("busName", bus.getBusName());
            busData.put("busNumber", bus.getBusNumber());
            busData.put("source", bus.getRoute().getSource());
            busData.put("destination", bus.getRoute().getDestination());
            busData.put("departureTime", bus.getDepartureTime());
            busData.put("arrivalTime", bus.getArrivalTime());
            busData.put("price", bus.getPrice());
            busData.put("totalSeats", bus.getTotalSeats());
            busData.put("availableSeats", available);
            busData.put("amenities", bus.getAmenities() != null ? Arrays.asList(bus.getAmenities().split(",")) : List.of());
            busData.put("boardingPoints", bus.getRoute().getBoardingPoints() != null ?
                    Arrays.asList(bus.getRoute().getBoardingPoints().split(",")) : List.of());
            busData.put("droppingPoints", bus.getRoute().getDroppingPoints() != null ?
                    Arrays.asList(bus.getRoute().getDroppingPoints().split(",")) : List.of());
            busData.put("rating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
            result.add(busData);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/buses/{id}/seats")
    public ResponseEntity<?> getSeats(@PathVariable Long id, @RequestParam String date) {
        Optional<Bus> busOpt = busService.getBusById(id);
        if (busOpt.isEmpty()) return ResponseEntity.notFound().build();
        Bus bus = busOpt.get();
        LocalDate travelDate = LocalDate.parse(date);
        List<Seat> seats = busService.getSeatsByBus(id);
        List<String> booked = busService.getBookedSeats(id, travelDate);

        List<Map<String, Object>> seatData = new ArrayList<>();
        for (Seat s : seats) {
            Map<String, Object> sd = new LinkedHashMap<>();
            sd.put("id", s.getId());
            sd.put("seatNumber", s.getSeatNumber());
            sd.put("seatType", s.getSeatType());
            sd.put("isBooked", booked.contains(s.getSeatNumber()));
            double price = busService.calculateFare(bus.getPrice(), s.getSeatType(), "ADULT");
            sd.put("price", price);
            seatData.add(sd);
        }
        return ResponseEntity.ok(seatData);
    }

    @GetMapping("/buses/{id}")
    public ResponseEntity<?> getBusDetails(@PathVariable Long id) {
        return busService.getBusById(id)
                .map(bus -> {
                    Map<String, Object> data = new LinkedHashMap<>();
                    data.put("id", bus.getId());
                    data.put("busName", bus.getBusName());
                    data.put("busNumber", bus.getBusNumber());
                    data.put("price", bus.getPrice());
                    data.put("totalSeats", bus.getTotalSeats());
                    data.put("amenities", bus.getAmenities());
                    data.put("departureTime", bus.getDepartureTime());
                    data.put("arrivalTime", bus.getArrivalTime());
                    data.put("source", bus.getRoute().getSource());
                    data.put("destination", bus.getRoute().getDestination());
                    data.put("boardingPoints", bus.getRoute().getBoardingPoints() != null ?
                            Arrays.asList(bus.getRoute().getBoardingPoints().split(",")) : List.of());
                    data.put("droppingPoints", bus.getRoute().getDroppingPoints() != null ?
                            Arrays.asList(bus.getRoute().getDroppingPoints().split(",")) : List.of());
                    return ResponseEntity.ok(data);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/routes")
    public ResponseEntity<?> getRoutes() {
        return ResponseEntity.ok(busService.getAllRoutes());
    }

    @GetMapping("/buses")
    public ResponseEntity<?> getBuses() {
        return ResponseEntity.ok(busService.getAllBuses());
    }
}
