package com.gcu.va.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.gcu.va.entity.BenefitStatus;
import com.gcu.va.entity.BenefitType;
import com.gcu.va.entity.Claim;
import com.gcu.va.entity.ClaimStatus;
import com.gcu.va.entity.Role;
import com.gcu.va.entity.User;
import com.gcu.va.entity.Veteran;

import com.gcu.va.repository.UserRepository;
import com.gcu.va.service.ClaimService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**

* Tests the claim-management functionality implemented by ClaimController.
*
* These tests verify claim viewing, claim submission, claim status updates,
* and the access restrictions applied to veterans and administrators.
*
* Mockito provides simulated dependencies so the controller can be tested
* without requiring actual database operations.
  */
  @ExtendWith(MockitoExtension.class)
  class ClaimControllerTest {

  @Mock
  private ClaimService claimService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private ClaimController claimController;

  private User veteranUser;
  private Veteran veteran;
  private Model model;

  /**

  * Initializes shared test data before each test executes.
    */
    @BeforeEach
    void setUp() {

    veteranUser = new User();
    veteranUser.setId(1L);
    veteranUser.setUsername("testveteran");
    veteranUser.setRole(Role.ROLE_VETERAN);

    veteran = new Veteran();
    veteran.setId(10L);
    veteran.setFirstName("Test");
    veteran.setLastName("Veteran");
    veteran.setUser(veteranUser);

    veteranUser.setVeteran(veteran);

    model = new ExtendedModelMap();

    when(authentication.getName()).thenReturn("testveteran");
    }

  /**

  * Verifies that a veteran can view their own claims.
    */
    @Test
    void veteranCanViewTheirOwnClaims() {

    when(userRepository.findByUsername("testveteran"))
    .thenReturn(veteranUser);

    when(claimService.getClaimsForVeteran(10L))
    .thenReturn(List.of(new Claim()));

    String result = claimController.viewClaims(authentication, model);

    assertEquals("claims", result);
    assertNotNull(model.getAttribute("claims"));

    verify(claimService).getClaimsForVeteran(10L);
    }

  /**

  * Verifies that an administrator can view all claims.
    */
    @Test
    void administratorCanViewAllClaims() {

    User admin = new User();
    admin.setUsername("admin");
    admin.setRole(Role.ROLE_ADMIN);

    when(authentication.getName()).thenReturn("admin");

    when(userRepository.findByUsername("admin"))
    .thenReturn(admin);

    when(claimService.getAllClaims())
    .thenReturn(List.of(new Claim()));

    String result = claimController.viewClaims(authentication, model);

    assertEquals("claims", result);
    assertNotNull(model.getAttribute("claims"));

    verify(claimService).getAllClaims();
    }

  /**

  * Verifies that a veteran can submit a benefit tracker.
  *
  * This test verifies the data passed to the service.
  * The mocked service does not execute its business logic.
    */
    @Test
    void veteranCanSubmitClaim() {

    when(userRepository.findByUsername("testveteran"))
    .thenReturn(veteranUser);

    String result = claimController.createClaim(
    "Disability",
    "Test disability claim",
    BenefitType.DISABILITY_COMPENSATION,
    BenefitStatus.SUBMITTED,
    "Initial submission",
    null,
    authentication
    );

    assertEquals("redirect:/claims", result);

    ArgumentCaptor<Claim> captor =
    ArgumentCaptor.forClass(Claim.class);

    verify(claimService).createBenefitTracker(
    captor.capture(),
    eq(BenefitType.DISABILITY_COMPENSATION),
    eq(BenefitStatus.SUBMITTED),
    eq("Initial submission"),
    isNull()
    );

    Claim submittedClaim = captor.getValue();

    assertEquals("Disability", submittedClaim.getClaimType());

    assertEquals(
    "Test disability claim",
    submittedClaim.getDescription()
    );

    assertEquals(veteran, submittedClaim.getVeteran());
    }

  /**

  * Verifies that an administrator cannot submit a veteran benefit tracker.
    */
    @Test
    void administratorCannotSubmitVeteranClaim() {

    User admin = new User();
    admin.setUsername("admin");
    admin.setRole(Role.ROLE_ADMIN);

    when(authentication.getName()).thenReturn("admin");

    when(userRepository.findByUsername("admin"))
    .thenReturn(admin);

    assertThrows(
    AccessDeniedException.class,
    () -> claimController.createClaim(
    "Disability",
    "Test claim",
    BenefitType.DISABILITY_COMPENSATION,
    BenefitStatus.SUBMITTED,
    null,
    null,
    authentication
    )
    );
    }

  /**

  * Verifies that an administrator can update a claim's status.
    */
    @Test
    void administratorCanUpdateClaimStatus() {

    User admin = new User();
    admin.setUsername("admin");
    admin.setRole(Role.ROLE_ADMIN);

    Claim claim = new Claim();
    claim.setId(1L);
    claim.setStatus(ClaimStatus.SUBMITTED);

    when(authentication.getName()).thenReturn("admin");

    when(userRepository.findByUsername("admin"))
    .thenReturn(admin);

    when(claimService.getById(1L))
    .thenReturn(claim);

    String result = claimController.updateClaimStatus(
    1L,
    "APPROVED",
    authentication
    );

    assertEquals("redirect:/admin", result);

    assertEquals(ClaimStatus.APPROVED, claim.getStatus());

    verify(claimService).saveClaim(claim);
    }

  /**

  * Verifies that a veteran cannot use the administrator status-update route.
    */
    @Test
    void veteranCannotUpdateClaimStatus() {

    when(userRepository.findByUsername("testveteran"))
    .thenReturn(veteranUser);

    assertThrows(
    AccessDeniedException.class,
    () -> claimController.updateClaimStatus(
    1L,
    "APPROVED",
    authentication
    )
    );
    }
    }
