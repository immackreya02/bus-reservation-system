package com.bus.service;

import com.bus.model.*;
import com.bus.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReservationService {

    @Autowired private ReservationRepository reservationRepository;
    @Autowired private BusRepository busRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RatingRepository ratingRepository;

    @Transactional
    public Reservation createReservation(Long userId, Long busId, LocalDate travelDate,
                                          String boardingPoint, String droppingPoint,
                                          List<Map<String, String>> passengers, double totalFare) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Bus bus = busRepository.findById(busId).orElseThrow(() -> new RuntimeException("Bus not found"));

        StringBuilder seatsBuilder = new StringBuilder();
        List<Passenger> passengerList = new ArrayList<>();

        for (Map<String, String> p : passengers) {
            String seatNum = p.get("seatNumber");
            if (seatsBuilder.length() > 0) seatsBuilder.append(",");
            seatsBuilder.append(seatNum);

            Passenger passenger = new Passenger();
            passenger.setName(p.get("name"));
            passenger.setAge(Integer.parseInt(p.getOrDefault("age", "25")));
            passenger.setType(p.get("type"));
            passenger.setSeatNumber(seatNum);
            passenger.setSeatType(p.get("seatType"));
            passengerList.add(passenger);
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBus(bus);
        reservation.setTravelDate(travelDate);
        reservation.setBookingDate(LocalDateTime.now());
        reservation.setBoardingPoint(boardingPoint);
        reservation.setDroppingPoint(droppingPoint);
        reservation.setSeatsBooked(seatsBuilder.toString());
        reservation.setTotalFare(totalFare);
        reservation.setStatus("CONFIRMED");
        reservation = reservationRepository.save(reservation);

        for (Passenger p : passengerList) {
            p.setReservation(reservation);
        }
        reservation.setPassengers(passengerList);
        return reservationRepository.save(reservation);
    }

    // Get all bookings for a user (no date filter)
    public List<Reservation> getUserReservations(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return reservationRepository.findByUser(user);
    }

    // Get bookings for a user filtered by date range
    public List<Reservation> getUserReservationsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId).orElseThrow();
        return reservationRepository.findByUserAndTravelDateBetween(user, startDate, endDate);
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    @Transactional
    public void cancelReservation(Long reservationId, Long userId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        if (!res.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        res.setStatus("CANCELLED");
        reservationRepository.save(res);
    }

    // Get all reservations (no date filter)
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // Get all reservations filtered by date range (admin)
    public List<Reservation> getAllReservationsByDateRange(LocalDate startDate, LocalDate endDate) {
        return reservationRepository.findByTravelDateBetween(startDate, endDate);
    }

    public Double getTotalRevenue() {
        Double revenue = reservationRepository.getTotalRevenue();
        return revenue != null ? revenue : 0.0;
    }

    public void ratebus(Long busId, Long userId, int rating, String comment) {
        Bus bus = busRepository.findById(busId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        Rating r = ratingRepository.findByBusAndUser(bus, user).orElse(new Rating());
        r.setBus(bus);
        r.setUser(user);
        r.setRating(rating);
        r.setComment(comment);
        ratingRepository.save(r);
    }

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }
}
