package com.gcu.va.controller;

import com.gcu.va.entity.User;
import com.gcu.va.entity.Veteran;
import com.gcu.va.repository.UserRepository;
import com.gcu.va.repository.VeteranRepository;
import com.gcu.va.service.EligibilityService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    private final UserRepository userRepository;
    private final VeteranRepository veteranRepository;
    private final EligibilityService eligibilityService;

    public ProfileController(UserRepository userRepository,
                             VeteranRepository veteranRepository, EligibilityService eligibilityService) {
        this.userRepository = userRepository;
        this.veteranRepository = veteranRepository;
        this.eligibilityService = eligibilityService;
    }

    @GetMapping("/my-profile")
    public String myProfile(Authentication auth, Model model) {

        String username = auth.getName();

        User user = userRepository.findByUsername(username);

        Veteran veteran = null;

        if (user != null) {
            veteran = veteranRepository.findByUserId(user.getId());
        }

        model.addAttribute("veteran", veteran);
        String status = eligibilityService.calculateStatus(veteran);
        model.addAttribute("eligibilityStatus", status);
        return "my-profile";
    }
}
