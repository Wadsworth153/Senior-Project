package com.gcu.va.controller;

import com.gcu.va.entity.Role;
import com.gcu.va.entity.User;
import com.gcu.va.repository.UserRepository;
import com.gcu.va.service.ClaimService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final ClaimService claimService;

    public AdminController(UserRepository userRepository, ClaimService claimService) {
        this.userRepository = userRepository;
        this.claimService = claimService;
    }

    // ADMIN DASHBOARD
    @GetMapping("/admin")
    public String adminDashboard(Authentication auth, Model model) {

        User user = userRepository.findByUsername(auth.getName());

        if (user.getRole() != Role.ROLE_ADMIN) {
            return "redirect:/home";
        }

        model.addAttribute("veterans", userRepository.findAll());
        model.addAttribute("claims", claimService.getAllClaims());

        return "admin-dashboard";
    }
    
    @GetMapping("/admin/search")
    public String searchVeterans(String username, Model model, Authentication auth) {

        User admin = userRepository.findByUsername(auth.getName());

        if (admin.getRole() != Role.ROLE_ADMIN) {
            return "redirect:/home";
        }

        model.addAttribute("users",
            userRepository.findByUsernameContaining(username)
        );

        model.addAttribute("claims", claimService.getAllClaims());

        return "admin-dashboard";
    }
}