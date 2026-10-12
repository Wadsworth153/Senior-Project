package com.gcu.va.service.impl;

// Import the Veteran entity representing a veteran's profile and service information.
import com.gcu.va.entity.*;

// Import repositories used to access veteran records in the database.
import com.gcu.va.repository.*;

// Import the VeteranService interface implemented by this class.
import com.gcu.va.service.*;

// Import Spring's Service annotation to register this class as a service bean.
import org.springframework.stereotype.Service;

// Import List to represent a collection of veteran records.
import java.util.List;

/**
 * Implements the business operations for managing veteran records.
 *
 * This service uses VeteranRepository to save veteran information and
 * retrieve veteran records from the database.
 */
@Service
public class VeteranServiceImpl implements VeteranService {

    // Repository used to save and retrieve veteran records.
    private final VeteranRepository repository;

    /**
     * Constructs the veteran service with its required repository.
     *
     * Spring supplies the repository through constructor-based dependency
     * injection.
     *
     * @param repository repository used to access veteran records
     */
    public VeteranServiceImpl(VeteranRepository repository) {

        // Store the injected repository for use by this service.
        this.repository = repository;
    }

    /**
     * Saves a veteran record to the database.
     *
     * The method prints debugging information before and after saving
     * the entity, then returns the saved entity.
     *
     * @param veteran the veteran record to save
     * @return the saved veteran record returned by the repository
     */
    @Override
    public Veteran saveVeteran(Veteran veteran) {

        // Print the veteran entity before saving for debugging purposes.
        System.out.println("SERVICE SAVE: " + veteran);

        // Save the veteran record and retain the returned entity.
        Veteran saved = repository.save(veteran);

        // Print the saved entity to help verify the save operation.
        System.out.println("SAVED ENTITY: " + saved);

        // Return the saved veteran record to the calling component.
        return saved;
    }

    /**
     * Retrieves all veteran records stored in the database.
     *
     * @return a list containing all veteran records returned by the repository
     */
    @Override
    public List<Veteran> getAllVeterans() {

        // Request all veteran records from the repository and return the list.
        return repository.findAll();
    }
}