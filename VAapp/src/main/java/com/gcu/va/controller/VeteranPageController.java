package com.gcu.va.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gcu.va.entity.Veteran;
import com.gcu.va.service.VeteranService;

@Controller
public class VeteranPageController {

    private final VeteranService service;

    public VeteranPageController(VeteranService service) {
        this.service = service;
    }

    @GetMapping("/veterans-page")
    public String showVeterans(Model model) {
        model.addAttribute("veterans", service.getAllVeterans());
        return "veterans";
    }
}
