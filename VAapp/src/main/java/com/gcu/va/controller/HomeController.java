package com.gcu.va.controller;

import com.gcu.va.entity.User;
import com.gcu.va.entity.Veteran;
import com.gcu.va.repository.UserRepository;
import com.gcu.va.service.EligibilityService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserRepository userRepository;
    private final EligibilityService eligibilityService;

    public HomeController(UserRepository userRepository, EligibilityService eligibilityService) {
        this.userRepository = userRepository;
        this.eligibilityService = eligibilityService;
    }

    @GetMapping("/home")
    public String home(Authentication authentication, Model model) {

        User user = userRepository.findByUsername(authentication.getName());
        Veteran veteran = user.getVeteran();

        model.addAttribute("user", user);
        model.addAttribute("veteran", veteran);

        String status = "N/A";
        String reason = "No veteran data";

        if (veteran != null) {

            status = eligibilityService.calculateStatus(veteran);

            if ("Eligible".equals(status)) {
                reason = "Meets service length and discharge requirements";
            } else {
                reason = "Does not meet eligibility requirements";
            }
        }

        model.addAttribute("eligibilityStatus", status);
        model.addAttribute("eligibilityReason", reason);

        return "home";
    }
}
