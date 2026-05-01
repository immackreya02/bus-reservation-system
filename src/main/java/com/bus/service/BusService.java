package com.bus.service;

import com.bus.model.*;
import com.bus.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BusService {

    @Autowired private BusRepository busRepository;
    @Autowired private RouteRepository routeRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private RatingRepository ratingRepository;

    public Route addRoute(String source, String destination, String boardingPoints, String droppingPoints) {
        Route route = new Route();
        route.setSource(source);
        route.setDestination(destination);
        route.setBoardingPoints(boardingPoints);
        route.setDroppingPoints(droppingPoints);
        return routeRepository.save(route);
    }

    public Bus addBus(Long routeId, String busName, String busNumber, double price,
                      int totalSeats, String amenities, String departureTime, String arrivalTime) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));
        Bus bus = new Bus();
        bus.setRoute(route);
        bus.setBusName(busName);
        bus.setBusNumber(busNumber);
        bus.setPrice(price);
        bus.setTotalSeats(totalSeats);
        bus.setAmenities(amenities);
        bus.setDepartureTime(departureTime);
        bus.setArrivalTime(arrivalTime);
        bus = busRepository.save(bus);

        // Create seats for this bus
        createSeatsForBus(bus, totalSeats);
        return bus;
    }

    private void createSeatsForBus(Bus bus, int totalSeats) {
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= totalSeats; i++) {
            Seat seat = new Seat();
            seat.setBus(bus);
            seat.setSeatNumber(String.valueOf(i));
            // Even-numbered seats are upper, odd are lower
            seat.setSeatType(i % 2 == 0 ? "UPPER" : "LOWER");
            seats.add(seat);
        }
        seatRepository.saveAll(seats);
    }

    public List<Bus> searchBuses(String source, String destination) {
        List<Route> routes = routeRepository.findBySourceIgnoreCaseAndDestinationIgnoreCase(source, destination);
        if (routes.isEmpty()) return Collections.emptyList();
        return busRepository.findByRouteIn(routes);
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    public Optional<Bus> getBusById(Long id) {
        return busRepository.findById(id);
    }

    public List<Seat> getSeatsByBus(Long busId) {
        return seatRepository.findByBusId(busId);
    }

    public List<String> getBookedSeats(Long busId, LocalDate travelDate) {
        List<Reservation> reservations = reservationRepository.findConfirmedByBusAndDate(busId, travelDate);
        List<String> booked = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getSeatsBooked() != null) {
                booked.addAll(Arrays.asList(r.getSeatsBooked().split(",")));
            }
        }
        return booked;
    }

    public double calculateFare(double basePrice, String seatType, String passengerType) {
        double fare = basePrice;
        // Seat type modifier
        if ("LOWER".equals(seatType)) fare *= 1.20;
        else if ("UPPER".equals(seatType)) fare *= 0.90;
        // Passenger type modifier
        if ("CHILD".equals(passengerType)) fare *= 0.50;
        else if ("INFANT".equals(passengerType)) fare = 0;
        return Math.round(fare * 100.0) / 100.0;
    }

    public Double getAverageRating(Long busId) {
        return ratingRepository.getAverageRatingByBusId(busId);
    }

    public List<Bus> getAllBusesWithRatings() {
        return busRepository.findAll();
    }
}
