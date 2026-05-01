package com.bus;

import com.bus.model.User;
import com.bus.repository.UserRepository;
import com.bus.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private BusService busService;

    @Override
    public void run(String... args) {
        // Create admin if not exists
        if (!userRepository.existsByEmail("admin@bus.com")) {
            User admin = new User("Admin", "admin@bus.com", passwordEncoder.encode("admin123"), "ADMIN");
            userRepository.save(admin);
        }

        // Seed sample routes and buses if none exist
        if (busService.getAllRoutes().isEmpty()) {
            // Route 1
            var r1 = busService.addRoute("Chennai", "Bangalore",
                    "Chennai Central,Tambaram,Chengalpet",
                    "Hosur,Electronic City,Silk Board,Majestic");

            // Route 2
            var r2 = busService.addRoute("Chennai", "Hyderabad",
                    "Chennai Central,Ambur,Vellore",
                    "Nellore,Ongole,Vijayawada,Hyderabad");

            // Route 3
            var r3 = busService.addRoute("Bangalore", "Mumbai",
                    "Majestic,Tumkur,Hubli",
                    "Pune,Dadar,Mumbai Central");

            // Buses for Route 1
            busService.addBus(r1.getId(), "KPN Travels", "TN01AB1234",
                    800, 40, "AC,WIFI,CHARGING", "09:00 PM", "05:00 AM");
            busService.addBus(r1.getId(), "SRM Travels", "TN02CD5678",
                    650, 36, "AC,CHARGING", "10:30 PM", "06:30 AM");
            busService.addBus(r1.getId(), "Parveen Travels", "TN03EF9012",
                    500, 44, "AC", "11:00 PM", "07:00 AM");

            // Buses for Route 2
            busService.addBus(r2.getId(), "APSRTC Garuda", "AP01GH3456",
                    1200, 40, "AC,WIFI", "06:00 PM", "08:00 AM");
            busService.addBus(r2.getId(), "Orange Travels", "TN04IJ7890",
                    950, 36, "AC,CHARGING", "07:30 PM", "10:00 AM");

            // Buses for Route 3
            busService.addBus(r3.getId(), "VRL Travels", "KA01KL2345",
                    1500, 40, "AC,WIFI,CHARGING", "05:00 PM", "07:00 AM");
        }
    }
}
