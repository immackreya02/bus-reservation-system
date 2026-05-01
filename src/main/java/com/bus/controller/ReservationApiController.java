package com.bus.controller;

import com.bus.model.*;
import com.bus.repository.UserRepository;
import com.bus.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ReservationApiController {

    @Autowired private ReservationService reservationService;
    @Autowired private UserRepository userRepository;

    @PostMapping("/book")
    public ResponseEntity<?> book(@RequestBody Map<String, Object> body, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        try {
            User user = userRepository.findByEmail(auth.getName()).orElseThrow();
            Long busId = Long.parseLong(body.get("busId").toString());
            LocalDate travelDate = LocalDate.parse(body.get("travelDate").toString());
            String boardingPoint = body.get("boardingPoint").toString();
            String droppingPoint = body.get("droppingPoint").toString();
            double totalFare = Double.parseDouble(body.get("totalFare").toString());

            @SuppressWarnings("unchecked")
            List<Map<String, String>> passengers = (List<Map<String, String>>) body.get("passengers");

            Reservation res = reservationService.createReservation(
                    user.getId(), busId, travelDate, boardingPoint, droppingPoint, passengers, totalFare);

            return ResponseEntity.ok(Map.of(
                "message", "Booking confirmed!",
                "reservationId", res.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // startDate and endDate are optional — if both provided, filter; otherwise return all
    @GetMapping("/my-bookings")
    public ResponseEntity<?> myBookings(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();

        List<Reservation> reservations;
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            reservations = reservationService.getUserReservationsByDateRange(user.getId(), start, end);
        } else {
            reservations = reservationService.getUserReservations(user.getId());
        }

        List<Map<String, Object>> result = reservations.stream().map(r -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", r.getId());
            m.put("busName", r.getBus().getBusName());
            m.put("busNumber", r.getBus().getBusNumber());
            m.put("source", r.getBus().getRoute().getSource());
            m.put("destination", r.getBus().getRoute().getDestination());
            m.put("travelDate", r.getTravelDate().toString());
            m.put("bookingDate", r.getBookingDate().toString());
            m.put("boardingPoint", r.getBoardingPoint());
            m.put("droppingPoint", r.getDroppingPoint());
            m.put("seatsBooked", r.getSeatsBooked());
            m.put("totalFare", r.getTotalFare());
            m.put("status", r.getStatus());
            m.put("busId", r.getBus().getId());
            List<Map<String, Object>> passengers = r.getPassengers() == null ? List.of() :
                r.getPassengers().stream().map(p -> {
                    Map<String, Object> pm = new LinkedHashMap<>();
                    pm.put("name", p.getName());
                    pm.put("type", p.getType());
                    pm.put("seatNumber", p.getSeatNumber());
                    pm.put("seatType", p.getSeatType());
                    return pm;
                }).collect(Collectors.toList());
            m.put("passengers", passengers);
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/reservation/{id}")
    public ResponseEntity<?> getReservation(@PathVariable Long id, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        Optional<Reservation> opt = reservationService.getReservationById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Reservation r = opt.get();
        User currentUser = userRepository.findByEmail(auth.getName()).orElseThrow();
        if (!r.getUser().getId().equals(currentUser.getId()) && !"ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", r.getId());
        m.put("busName", r.getBus().getBusName());
        m.put("busNumber", r.getBus().getBusNumber());
        m.put("source", r.getBus().getRoute().getSource());
        m.put("destination", r.getBus().getRoute().getDestination());
        m.put("departureTime", r.getBus().getDepartureTime());
        m.put("arrivalTime", r.getBus().getArrivalTime());
        m.put("travelDate", r.getTravelDate().toString());
        m.put("bookingDate", r.getBookingDate().toString());
        m.put("boardingPoint", r.getBoardingPoint());
        m.put("droppingPoint", r.getDroppingPoint());
        m.put("seatsBooked", r.getSeatsBooked());
        m.put("totalFare", r.getTotalFare());
        m.put("status", r.getStatus());
        m.put("userName", r.getUser().getName());
        m.put("userEmail", r.getUser().getEmail());
        List<Map<String, Object>> passengers = r.getPassengers() == null ? List.of() :
            r.getPassengers().stream().map(p -> {
                Map<String, Object> pm = new LinkedHashMap<>();
                pm.put("name", p.getName());
                pm.put("age", p.getAge());
                pm.put("type", p.getType());
                pm.put("seatNumber", p.getSeatNumber());
                pm.put("seatType", p.getSeatType());
                return pm;
            }).collect(Collectors.toList());
        m.put("passengers", passengers);
        return ResponseEntity.ok(m);
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        try {
            User user = userRepository.findByEmail(auth.getName()).orElseThrow();
            reservationService.cancelReservation(id, user.getId());
            return ResponseEntity.ok(Map.of("message", "Reservation cancelled successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/rate")
    public ResponseEntity<?> rate(@RequestBody Map<String, Object> body, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        try {
            User user = userRepository.findByEmail(auth.getName()).orElseThrow();
            Long busId = Long.parseLong(body.get("busId").toString());
            int rating = Integer.parseInt(body.get("rating").toString());
            String comment = body.getOrDefault("comment", "").toString();
            reservationService.ratebus(busId, user.getId(), rating, comment);
            return ResponseEntity.ok(Map.of("message", "Rating submitted!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
