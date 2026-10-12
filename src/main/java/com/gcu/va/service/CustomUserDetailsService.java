package com.gcu.va.service;

// Import Spring Security's UserDetails interface, which represents
// the user information required for authentication and authorization.
import org.springframework.security.core.userdetails.UserDetails;

// Import the interface that this custom service implements.
import org.springframework.security.core.userdetails.UserDetailsService;

// Import the exception Spring Security uses when a username cannot be found.
import org.springframework.security.core.userdetails.UsernameNotFoundException;

// Import Spring's Service annotation to register this class as a service bean.
import org.springframework.stereotype.Service;

// Import the User entity that stores application account information.
import com.gcu.va.entity.User;

// Import the repository used to retrieve user accounts from the database.
import com.gcu.va.repository.UserRepository;

/**
 * Loads application user information for Spring Security authentication.
 *
 * This service retrieves user accounts from the database and converts
 * them into Spring Security UserDetails objects. The returned information
 * includes the username, encoded password, and assigned role.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Repository used to locate registered user accounts.
    private final UserRepository repository;

    /**
     * Constructs the service with its required user repository.
     *
     * Spring provides the repository through constructor-based dependency
     * injection.
     *
     * @param repository repository used to retrieve user accounts
     */
    public CustomUserDetailsService(UserRepository repository) {

        // Store the injected repository for use during authentication.
        this.repository = repository;
    }

    /**
     * Loads a user's authentication information using their username.
     *
     * This method retrieves the user account from the database. If the
     * username does not exist, it throws UsernameNotFoundException.
     * Otherwise, it builds a Spring Security UserDetails object containing
     * the username, stored password hash, and assigned role.
     *
     * @param username username supplied during authentication
     * @return Spring Security details for the matching user account
     * @throws UsernameNotFoundException if no account matches the username
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // Search the database for an account with the supplied username.
        User user = repository.findByUsername(username);

        // Stop authentication if no matching account exists.
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        // Build the Spring Security user object using the account's
        // username, encoded password, and assigned role.
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())

                // Supply the stored password hash for Spring Security
                // to verify against the password entered during login.
                .password(user.getPassword())

                // Remove the ROLE_ prefix because Spring Security's roles()
                // method adds the appropriate prefix when creating authorities.
                .roles(user.getRole().name().replace("ROLE_", ""))

                // Finish building and return the authentication user object.
                .build();
    }
}