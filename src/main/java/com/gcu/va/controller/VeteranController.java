package com.gcu.va.controller;

// Import the Veteran entity representing a veteran's profile.
import com.gcu.va.entity.*;

// Import the service layer used to manage veteran records.
import com.gcu.va.service.*;

// Import Spring annotations for handling REST requests and mapping endpoints.
import org.springframework.web.bind.annotation.*;

// Import List to represent a collection of veteran records.
import java.util.List;

/**
 * Provides REST API endpoints for managing veteran records.
 *
 * This controller accepts HTTP requests to create veteran records
 * and retrieve the collection of existing veteran records.
 * It delegates data operations to VeteranService.
 */
@RestController
@RequestMapping("/api/veterans")
public class VeteranController {

    // Service responsible for saving and retrieving veteran records.
    private final VeteranService service;

    /**
     * Constructs the controller with its required veteran service.
     *
     * Spring supplies the service through constructor-based dependency injection.
     *
     * @param service service used to manage veteran records
     */
    public VeteranController(VeteranService service) {

        // Store the injected veteran service.
        this.service = service;
    }

    /**
     * Creates a veteran record using the JSON request body.
     *
     * The submitted JSON is converted into a Veteran entity, which is
     * passed to the service layer for saving.
     *
     * @param veteran veteran information supplied in the request body
     * @return the saved veteran record
     */
    @PostMapping
    public Veteran createVeteran(@RequestBody Veteran veteran) {

        // Delegate saving the submitted veteran record to the service layer.
        return service.saveVeteran(veteran);
    }

    /**
     * Retrieves all veteran records.
     *
     * This endpoint delegates the retrieval operation to VeteranService
     * and returns the resulting collection as a JSON response.
     *
     * @return a list of veteran records
     */
    @GetMapping
    public List<Veteran> getAllVeterans() {

        // Retrieve and return all veteran records through the service layer.
        return service.getAllVeterans();
    }
}