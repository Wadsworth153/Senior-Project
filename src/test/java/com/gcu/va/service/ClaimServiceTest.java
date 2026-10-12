package com.gcu.va.service;

// Import JUnit assertions for checking expected values and exceptions.
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Import Mockito's argument matcher and verification methods.
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Import List and Optional for representing repository results.
import java.util.List;
import java.util.Optional;

// Import the claim entity, claim status enum, and repository.
import com.gcu.va.entity.Claim;
import com.gcu.va.entity.ClaimStatus;
import com.gcu.va.repository.ClaimRepository;

// Import JUnit annotations for setup methods and test cases.
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// Import Mockito annotations for creating and injecting mock dependencies.
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests the business logic implemented by ClaimService.
 *
 * These tests verify retrieval of claims for a veteran, retrieval of all
 * claims, exclusion of null claim entries, saving claims, and handling
 * requests for claims that do not exist.
 *
 * Mockito simulates repository operations so the service can be tested
 * independently of the actual database.
 */
@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    // Mock the repository used to access claim records.
    @Mock
    private ClaimRepository claimRepository;

    // Create the service and inject its mocked repository dependency.
    @InjectMocks
    private ClaimService claimService;

    // Store a reusable claim for the test cases.
    private Claim claim;

    /**
     * Initializes a sample claim before each test executes.
     *
     * The claim represents a submitted disability claim with a known
     * identifier and description.
     */
    @BeforeEach
    void setUp() {

        // Create a sample claim entity.
        claim = new Claim();

        // Assign an identifier to the claim.
        claim.setId(1L);

        // Set the claim type.
        claim.setClaimType("Disability");

        // Set the initial claim status.
        claim.setStatus(ClaimStatus.SUBMITTED);

        // Provide a description for the claim.
        claim.setDescription("Test disability claim");
    }

    /**
     * Verifies that the service retrieves claims associated with a veteran.
     *
     * The repository is configured to return one claim, and the test checks
     * the returned list and verifies that the correct repository method is used.
     */
    @Test
    void getClaimsForVeteranReturnsVeteransClaims() {

        // Define the identifier of the veteran whose claims are requested.
        Long veteranId = 10L;

        // Configure the repository to return the sample claim for this veteran.
        when(claimRepository.findByVeteranId(veteranId))
                .thenReturn(List.of(claim));

        // Retrieve the claims associated with the veteran.
        List<Claim> result = claimService.getClaimsForVeteran(veteranId);

        // Verify that exactly one claim is returned.
        assertEquals(1, result.size());

        // Verify that the returned claim has the expected type.
        assertEquals("Disability", result.get(0).getClaimType());

        // Verify that the service queried the repository using the veteran ID.
        verify(claimRepository).findByVeteranId(veteranId);
    }

    /**
     * Verifies that the service retrieves all claims from the repository.
     *
     * The repository is configured to return two different claims, and
     * the test checks that both are present in the returned list.
     */
    @Test
    void getAllClaimsReturnsClaimsFromRepository() {

        // Create a second sample claim.
        Claim secondClaim = new Claim();

        // Assign an identifier to the second claim.
        secondClaim.setId(2L);

        // Set the second claim's type.
        secondClaim.setClaimType("Education");

        // Set the second claim's initial status.
        secondClaim.setStatus(ClaimStatus.SUBMITTED);

        // Configure the repository to return both sample claims.
        when(claimRepository.findAll())
                .thenReturn(java.util.Arrays.asList(claim, secondClaim));

        // Retrieve all claims through the service.
        List<Claim> result = claimService.getAllClaims();

        // Verify that two claims are returned.
        assertEquals(2, result.size());

        // Verify the type of the first returned claim.
        assertEquals("Disability", result.get(0).getClaimType());

        // Verify the type of the second returned claim.
        assertEquals("Education", result.get(1).getClaimType());

        // Verify that the service requested all claims from the repository.
        verify(claimRepository).findAll();
    }

    /**
     * Verifies that null entries are excluded from the list returned
     * by the service.
     *
     * The repository returns one valid claim and one null entry.
     * The test checks that the service returns only the valid claim.
     */
    @Test
    void getAllClaimsExcludesNullEntries() {

        // Configure the repository to return a claim and a null entry.
        when(claimRepository.findAll())
                .thenReturn(java.util.Arrays.asList(claim, null));

        // Retrieve all claims through the service.
        List<Claim> result = claimService.getAllClaims();

        // Verify that only one entry remains in the returned list.
        assertEquals(1, result.size());

        // Verify that the remaining entry is not null.
        assertNotNull(result.get(0));
    }

    /**
     * Verifies that the service saves a claim and returns the saved entity.
     *
     * The repository mock returns the sample claim after its save method
     * is called, allowing the test to check the returned result.
     */
    @Test
    void saveClaimSavesAndReturnsClaim() {

        // Configure the repository to return the sample claim when saving.
        when(claimRepository.save(any(Claim.class)))
                .thenReturn(claim);

        // Save the claim through the service.
        Claim result = claimService.saveClaim(claim);

        // Verify that the service returns a claim.
        assertNotNull(result);

        // Verify that the returned claim has the expected identifier.
        assertEquals(1L, result.getId());

        // Verify that the returned claim has the expected status.
        assertEquals(ClaimStatus.SUBMITTED, result.getStatus());

        // Verify that the service passed the sample claim to the repository.
        verify(claimRepository).save(claim);
    }

    /**
     * Verifies that the service returns a claim when its identifier exists.
     *
     * The repository is configured to return the sample claim wrapped
     * in an Optional.
     */
    @Test
    void getByIdReturnsClaimWhenItExists() {

        // Configure the repository to return the sample claim for ID 1.
        when(claimRepository.findById(1L))
                .thenReturn(Optional.of(claim));

        // Retrieve the claim through the service.
        Claim result = claimService.getById(1L);

        // Verify that a claim is returned.
        assertNotNull(result);

        // Verify that the returned claim has the expected identifier.
        assertEquals(1L, result.getId());

        // Verify that the returned claim has the expected type.
        assertEquals("Disability", result.getClaimType());

        // Verify that the service queried the repository using the claim ID.
        verify(claimRepository).findById(1L);
    }

    /**
     * Verifies that the service throws an exception when a claim does not exist.
     *
     * The repository returns an empty Optional for the requested identifier.
     * The test checks that the service throws a RuntimeException with the
     * expected error message.
     */
    @Test
    void getByIdThrowsExceptionWhenClaimDoesNotExist() {

        // Configure the repository to indicate that claim 999 does not exist.
        when(claimRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Verify that requesting the missing claim throws an exception.
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> claimService.getById(999L)
        );

        // Verify that the exception contains the expected message.
        assertEquals(
                "Claim not found with id: 999",
                exception.getMessage()
        );
    }
}