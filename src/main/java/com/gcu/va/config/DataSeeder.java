package com.gcu.va.config;

// Import the Role enum used to assign the administrator role.
import com.gcu.va.entity.Role;

// Import the User entity representing an application account.
import com.gcu.va.entity.User;

// Import the repository used to find and save user accounts.
import com.gcu.va.repository.UserRepository;

// Import the interface for executing code when the Spring Boot application starts.
import org.springframework.boot.CommandLineRunner;

// Import the service used to encode passwords securely.
import org.springframework.security.crypto.password.PasswordEncoder;

// Import the annotation that registers this class as a Spring-managed component.
import org.springframework.stereotype.Component;

/**
 * Initializes required application data when the application starts.
 *
 * This component implements CommandLineRunner so that its run method
 * executes during Spring Boot startup. It checks for an existing
 * administrator account and creates one if no matching username exists.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    // Repository used to search for and save user accounts.
    private final UserRepository userRepository;

    // Password encoder used to encode the administrator password.
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs the data seeder with its required dependencies.
     *
     * Spring supplies the repository and password encoder through
     * constructor-based dependency injection.
     *
     * @param userRepository repository for accessing user records
     * @param passwordEncoder encoder used to encode passwords
     */
    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        // Store the injected user repository.
        this.userRepository = userRepository;

        // Store the injected password encoder.
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Checks for the default administrator account and creates it if needed.
     *
     * This method runs when the Spring Boot application starts.
     * If a user with the username "admin" does not exist, the method
     * creates an administrator account, encodes its password, assigns
     * the administrator role, and saves the account to the database.
     *
     * @param args command-line arguments supplied during application startup
     */
    @Override
    public void run(String... args) {

        // Prevent duplicate administrator creation by checking the username.
        if (userRepository.findByUsername("admin") == null) {

            // Create a new user entity for the administrator account.
            User admin = new User();

            // Assign the default administrator username.
            admin.setUsername("admin");

            // Encode the password before storing it in the database.
            admin.setPassword(passwordEncoder.encode("admin123"));

            // Assign the administrator role for access control.
            admin.setRole(Role.ROLE_ADMIN);

            // Save the administrator account to the database.
            userRepository.save(admin);
        }
    }
}