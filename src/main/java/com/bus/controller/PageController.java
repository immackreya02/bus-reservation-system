package com.bus.controller;

import com.bus.model.User;
import com.bus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class PageController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home() {
        return "redirect:/search";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/search")
    public String searchPage(Authentication auth, Model model) {
        if (auth != null) {
            Optional<User> user = userRepository.findByEmail(auth.getName());
            user.ifPresent(u -> model.addAttribute("currentUser", u));
        }
        return "search";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        if (auth == null) return "redirect:/login";
        Optional<User> user = userRepository.findByEmail(auth.getName());
        if (user.isPresent() && "ADMIN".equals(user.get().getRole())) {
            return "redirect:/admin";
        }
        return "redirect:/search";
    }

    @GetMapping("/booking")
    public String bookingPage(Authentication auth, Model model) {
        if (auth == null) return "redirect:/login";
        Optional<User> user = userRepository.findByEmail(auth.getName());
        user.ifPresent(u -> model.addAttribute("currentUser", u));
        return "booking";
    }

    @GetMapping("/history")
    public String historyPage(Authentication auth, Model model) {
        if (auth == null) return "redirect:/login";
        Optional<User> user = userRepository.findByEmail(auth.getName());
        user.ifPresent(u -> model.addAttribute("currentUser", u));
        return "history";
    }

    @GetMapping("/invoice")
    public String invoicePage(Authentication auth, Model model) {
        if (auth == null) return "redirect:/login";
        Optional<User> user = userRepository.findByEmail(auth.getName());
        user.ifPresent(u -> model.addAttribute("currentUser", u));
        return "invoice";
    }

    @GetMapping("/profile")
    public String profilePage(Authentication auth, Model model) {
        if (auth == null) return "redirect:/login";
        Optional<User> user = userRepository.findByEmail(auth.getName());
        user.ifPresent(u -> model.addAttribute("currentUser", u));
        return "profile";
    }

    @GetMapping("/admin")
    public String adminPage(Authentication auth, Model model) {
        if (auth == null) return "redirect:/login";
        Optional<User> user = userRepository.findByEmail(auth.getName());
        if (user.isEmpty() || !"ADMIN".equals(user.get().getRole())) {
            return "redirect:/search";
        }
        model.addAttribute("currentUser", user.get());
        return "admin";
    }
}
