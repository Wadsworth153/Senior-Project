package com.gcu.va.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gcu.va.dto.RegistrationForm;
import com.gcu.va.entity.Role;
import com.gcu.va.entity.User;
import com.gcu.va.entity.Veteran;
import com.gcu.va.repository.UserRepository;
import com.gcu.va.repository.VeteranRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Unit tests for RegistrationServiceImpl.
 *
 * These tests verify that the registration service correctly creates user
 * accounts and veteran profiles, securely encodes passwords, rejects
 * duplicate usernames, validates password confirmation, and links a veteran
 * profile to the user account created during registration.
 *
 * Mockito is used to simulate repository and password-encoder dependencies
 * without requiring a live database or actual password-encoding operations.
 */
@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    /**
     * Mocked repository responsible for storing and retrieving user accounts.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * Mocked repository responsible for storing veteran profile information.
     */
    @Mock
    private VeteranRepository veteranRepository;

    /**
     * Mocked password encoder used to simulate secure password encoding.
     */
    @Mock
    private PasswordEncoder passwordEncoder;

    /**
     * Registration service under test.
     *
     * Mockito injects the mocked dependencies into this service instance.
     */
    @InjectMocks
    private RegistrationServiceImpl registrationService;

    /**
     * Registration form populated with valid default test data.
     */
    private RegistrationForm form;

    /**
     * Initializes the registration form before each test.
     *
     * A valid form is created so individual tests can focus on a particular
     * registration scenario without repeating the common setup.
     */
    @BeforeEach
    void setUp() {

        // Create a new registration form for the current test.
        form = new RegistrationForm();

        // Populate the username and password fields.
        form.setUsername("testveteran");
        form.setPassword("Password123!");
        form.setConfirmPassword("Password123!");

        // Populate the veteran's personal information.
        form.setFirstName("Test");
        form.setLastName("Veteran");
        form.setEmail("test@example.com");
    }

    /**
     * Tests that valid registration creates a user account and a veteran
     * profile with the expected account information.
     */
    @Test
    void validRegistrationCreatesUserAndVeteranProfile() {

        // Simulate a username lookup that finds no existing account.
        when(userRepository.findByUsername("testveteran")).thenReturn(null);

        // Simulate password encoding.
        when(passwordEncoder.encode("Password123!")).thenReturn("encoded-password");

        // Simulate saving the user and assigning a generated database ID.
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Execute the registration operation.
        User result = registrationService.registerVeteran(form);

        // Verify that the created user has the expected username.
        assertEquals("testveteran", result.getUsername());

        // Verify that the account receives the veteran role.
        assertEquals(Role.ROLE_VETERAN, result.getRole());

        // Verify that the stored password is the encoded value.
        assertEquals("encoded-password", result.getPassword());

        // Verify that the user account is saved.
        verify(userRepository).save(any(User.class));

        // Verify that the veteran profile is also saved.
        verify(veteranRepository).save(any(Veteran.class));
    }

    /**
     * Tests that registration stores an encoded password rather than the
     * original plaintext password.
     */
    @Test
    void registrationEncodesPasswordInsteadOfSavingPlaintext() {

        // Simulate a username lookup that finds no existing account.
        when(userRepository.findByUsername("testveteran")).thenReturn(null);

        // Configure the password encoder to return a simulated encoded value.
        when(passwordEncoder.encode("Password123!")).thenReturn("encoded-password");

        // Simulate saving the user account.
        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Execute registration.
        registrationService.registerVeteran(form);

        // Capture the user object passed to the repository for saving.
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        // Confirm that the saved password is not the original plaintext value.
        assertNotEquals("Password123!", captor.getValue().getPassword());

        // Confirm that the saved password matches the simulated encoded value.
        assertEquals("encoded-password", captor.getValue().getPassword());
    }

    /**
     * Tests that registration rejects a username that already belongs to
     * an existing user account.
     */
    @Test
    void duplicateUsernameIsRejected() {

        // Create an existing user with the requested username.
        User existingUser = new User();
        existingUser.setUsername("testveteran");

        // Simulate finding the existing account in the repository.
        when(userRepository.findByUsername("testveteran")).thenReturn(existingUser);

        // Verify that registration throws an exception for the duplicate username.
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> registrationService.registerVeteran(form)
        );

        // Verify that the exception contains the expected message.
        assertEquals("Username already exists", exception.getMessage());

        // Confirm that no new user account is saved.
        verify(userRepository, never()).save(any(User.class));

        // Confirm that no veteran profile is saved.
        verify(veteranRepository, never()).save(any(Veteran.class));
    }

    /**
     * Tests that registration rejects a form when the password and
     * confirmation password do not match.
     */
    @Test
    void mismatchedPasswordsAreRejected() {

        // Change the confirmation password so it differs from the password.
        form.setConfirmPassword("DifferentPassword123!");

        // Verify that registration throws an exception for the mismatch.
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> registrationService.registerVeteran(form)
        );

        // Verify that the exception contains the expected message.
        assertEquals("Passwords do not match", exception.getMessage());

        // Confirm that no user account is saved.
        verify(userRepository, never()).save(any(User.class));

        // Confirm that no veteran profile is saved.
        verify(veteranRepository, never()).save(any(Veteran.class));
    }

    /**
     * Tests that the veteran profile created during registration is populated
     * with the submitted personal information and linked to the new user.
     */
    @Test
    void veteranProfileIsLinkedToCreatedUser() {

        // Simulate a username lookup that finds no existing account.
        when(userRepository.findByUsername("testveteran")).thenReturn(null);

        // Simulate password encoding.
        when(passwordEncoder.encode("Password123!")).thenReturn("encoded-password");

        // Simulate saving the user and assigning a generated database ID.
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Execute registration to create the user and veteran profile.
        registrationService.registerVeteran(form);

        // Capture the veteran profile passed to the repository.
        ArgumentCaptor<Veteran> captor = ArgumentCaptor.forClass(Veteran.class);
        verify(veteranRepository).save(captor.capture());

        // Verify that the profile contains the submitted first name.
        assertEquals("Test", captor.getValue().getFirstName());

        // Verify that the profile contains the submitted last name.
        assertEquals("Veteran", captor.getValue().getLastName());

        // Verify that the profile contains the submitted email address.
        assertEquals("test@example.com", captor.getValue().getEmail());

        // Verify that the profile is linked to the newly created user account.
        assertEquals("testveteran", captor.getValue().getUser().getUsername());
    }
}