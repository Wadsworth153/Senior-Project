package com.gcu.va.service;

// Import the Claim entity representing a veteran's benefit claim.
import com.gcu.va.entity.Claim;

// Import the repository responsible for accessing claim records in the database.
import com.gcu.va.repository.ClaimRepository;

// Import Spring's Service annotation to register this class as a service bean.
import org.springframework.stereotype.Service;

// Import List to represent collections of claim records.
import java.util.List;

/**
 * Provides business logic for retrieving and saving veteran claim records.
 *
 * This service communicates with ClaimRepository to perform claim-related
 * data operations. Controllers can use this service without directly
 * accessing the repository.
 */
@Service
public class ClaimService {

    // Repository used to retrieve and persist claim records.
    private final ClaimRepository claimRepository;

    /**
     * Creates the claim service with its required repository dependency.
     *
     * Constructor-based dependency injection allows Spring to supply the
     * repository when creating this service.
     *
     * @param claimRepository repository used to access claim records
     */
    public ClaimService(ClaimRepository claimRepository) {

        // Store the injected repository for use by the service methods.
        this.claimRepository = claimRepository;
    }

    /**
     * Retrieves all claims associated with a specific veteran.
     *
     * @param veteranId unique identifier of the veteran
     * @return list of claims associated with the specified veteran
     */
    public List<Claim> getClaimsForVeteran(Long veteranId) {

        // Query the repository for claims belonging to the specified veteran.
        return claimRepository.findByVeteranId(veteranId);
    }

    /**
     * Retrieves all claim records and removes any null entries.
     *
     * @return list containing all non-null claim records
     */
    public List<Claim> getAllClaims() {

        // Retrieve all claims, create a stream, filter out null entries,
        // and collect the remaining claims into a list.
        return claimRepository.findAll()
                .stream()
                .filter(c -> c != null)
                .toList();
    }

    /**
     * Saves a claim record to the database.
     *
     * The repository determines whether the claim is inserted as a new
     * record or updated as an existing record.
     *
     * @param claim claim record to save
     * @return the saved claim returned by the repository
     */
    public Claim saveClaim(Claim claim) {

        // Delegate the persistence operation to the claim repository.
        return claimRepository.save(claim);
    }

    /**
     * Retrieves a claim using its unique identifier.
     *
     * If no claim exists with the supplied identifier, the method throws
     * a RuntimeException containing the requested identifier.
     *
     * @param id unique identifier of the claim
     * @return the matching claim record
     * @throws RuntimeException if no claim exists with the supplied ID
     */
    public Claim getById(Long id) {

        // Search for the claim by ID. If the repository returns an empty
        // Optional, throw an exception to indicate that the claim was not found.
        return claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Claim not found with id: " + id));
    }
}