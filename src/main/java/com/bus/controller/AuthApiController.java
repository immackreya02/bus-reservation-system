package com.bus.controller;

import com.bus.model.User;
import com.bus.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthApiController {

    @Autowired private UserService userService;
    @Autowired private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            String name = body.get("name");
            String email = body.get("email");
            String password = body.get("password");
            if (name == null || email == null || password == null || name.isBlank() || email.isBlank() || password.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "All fields are required"));
            }
            User user = userService.register(name, email, password);
            return ResponseEntity.ok(Map.of("message", "Registered successfully", "userId", user.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        }
        Optional<User> user = userService.findByEmail(auth.getName());
        if (user.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        User u = user.get();
        return ResponseEntity.ok(Map.of(
            "id", u.getId(),
            "name", u.getName(),
            "email", u.getEmail(),
            "role", u.getRole()
        ));
    }
}
