package com.gcu.va.controller;

import com.gcu.va.entity.*;
import com.gcu.va.service.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veterans")
public class VeteranController {

    private final VeteranService service;

    public VeteranController(VeteranService service) {
        this.service = service;
    }

    @PostMapping
    public Veteran createVeteran(@RequestBody Veteran veteran) {
        return service.saveVeteran(veteran);
    }

    @GetMapping
    public List<Veteran> getAllVeterans() {
        return service.getAllVeterans();
    }
}
