package com.gcu.va.controller;

import com.gcu.va.dto.RegistrationForm;
import com.gcu.va.service.RegistrationService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("form", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute RegistrationForm form) {

    	System.out.println("=== DEBUG DTO ===");
        System.out.println("DOB = " + form.getDateOfBirth());
        System.out.println("START = " + form.getServiceStartDate());
        System.out.println("END = " + form.getServiceEndDate());
        
        registrationService.registerVeteran(form);

        return "redirect:/login?registered";
    }
}
