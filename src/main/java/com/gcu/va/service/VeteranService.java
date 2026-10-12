package com.gcu.va.service;

// Import the Veteran entity used by the service methods.
import com.gcu.va.entity.*;

// Import List to represent a collection of veteran records.
import java.util.List;

/**
 * Defines the service operations available for managing veteran records.
 *
 * Implementing classes provide the logic for saving veteran information
 * and retrieving veteran records from the database.
 */
public interface VeteranService {

    /**
     * Saves a veteran record.
     *
     * The implementing class is responsible for performing the save
     * operation through the appropriate repository.
     *
     * @param veteran the veteran record to save
     * @return the saved veteran record
     */
    Veteran saveVeteran(Veteran veteran);

    /**
     * Retrieves all veteran records.
     *
     * The implementing class determines how the records are retrieved.
     *
     * @return a list of veteran records
     */
    List<Veteran> getAllVeterans();
}