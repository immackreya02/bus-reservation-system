package com.bus.controller;

import com.bus.model.*;
import com.bus.repository.*;
import com.bus.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    @Autowired private BusService busService;
    @Autowired private ReservationService reservationService;
    @Autowired private RatingRepository ratingRepository;
    @Autowired private BusRepository busRepository;

    @PostMapping("/route")
    public ResponseEntity<?> addRoute(@RequestBody Map<String, String> body) {
        try {
            Route r = busService.addRoute(
                body.get("source"), body.get("destination"),
                body.get("boardingPoints"), body.get("droppingPoints"));
            return ResponseEntity.ok(Map.of("message", "Route added", "id", r.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/bus")
    public ResponseEntity<?> addBus(@RequestBody Map<String, Object> body) {
        try {
            Long routeId = Long.parseLong(body.get("routeId").toString());
            String busName = body.get("busName").toString();
            String busNumber = body.get("busNumber").toString();
            double price = Double.parseDouble(body.get("price").toString());
            int totalSeats = Integer.parseInt(body.get("totalSeats").toString());
            String amenities = body.get("amenities").toString();
            String departure = body.get("departureTime").toString();
            String arrival = body.get("arrivalTime").toString();
            Bus bus = busService.addBus(routeId, busName, busNumber, price, totalSeats, amenities, departure, arrival);
            return ResponseEntity.ok(Map.of("message", "Bus added", "id", bus.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // startDate and endDate are optional — if both provided, filter by travelDate range
    @GetMapping("/reservations")
    public ResponseEntity<?> getAllReservations(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<Reservation> reservations;
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            reservations = reservationService.getAllReservationsByDateRange(start, end);
        } else {
            reservations = reservationService.getAllReservations();
        }

        List<Map<String, Object>> result = reservations.stream().map(r -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", r.getId());
            m.put("userName", r.getUser().getName());
            m.put("userEmail", r.getUser().getEmail());
            m.put("busName", r.getBus().getBusName());
            m.put("source", r.getBus().getRoute().getSource());
            m.put("destination", r.getBus().getRoute().getDestination());
            m.put("travelDate", r.getTravelDate().toString());
            m.put("bookingDate", r.getBookingDate().toString());
            m.put("seatsBooked", r.getSeatsBooked());
            m.put("totalFare", r.getTotalFare());
            m.put("status", r.getStatus());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenue() {
        double total = reservationService.getTotalRevenue();
        List<Reservation> all = reservationService.getAllReservations();
        long confirmed = all.stream().filter(r -> "CONFIRMED".equals(r.getStatus())).count();
        long cancelled = all.stream().filter(r -> "CANCELLED".equals(r.getStatus())).count();
        return ResponseEntity.ok(Map.of(
            "totalRevenue", total,
            "confirmedBookings", confirmed,
            "cancelledBookings", cancelled,
            "totalBookings", all.size()
        ));
    }

    @GetMapping("/ratings")
    public ResponseEntity<?> getRatings() {
        List<Bus> buses = busService.getAllBuses();
        List<Map<String, Object>> result = buses.stream().map(bus -> {
            Double avg = busService.getAverageRating(bus.getId());
            List<Rating> ratings = ratingRepository.findByBus(bus);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("busId", bus.getId());
            m.put("busName", bus.getBusName());
            m.put("busNumber", bus.getBusNumber());
            m.put("averageRating", avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
            m.put("totalRatings", ratings.size());
            m.put("ratings", ratings.stream().map(r -> Map.of(
                "user", r.getUser().getName(),
                "rating", r.getRating(),
                "comment", r.getComment() != null ? r.getComment() : ""
            )).collect(Collectors.toList()));
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
