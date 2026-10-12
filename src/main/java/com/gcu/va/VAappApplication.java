package com.gcu.va;

// Import Spring Boot's application startup utility.
import org.springframework.boot.SpringApplication;

// Import the annotation that enables Spring Boot auto-configuration
// and component scanning for the application.
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the VA Benefits and Eligibility Tracking System.
 *
 * The SpringBootApplication annotation enables Spring Boot auto-configuration
 * and component scanning so that the application's controllers, services,
 * repositories, and configuration classes can be discovered and initialized.
 */
@SpringBootApplication
public class VAappApplication {

    /**
     * Starts the Spring Boot application.
     *
     * This method initializes the Spring application context and starts
     * the embedded web server using the application's configured settings.
     *
     * @param args command-line arguments supplied when starting the application
     */
    public static void main(String[] args) {

        // Launch the application and initialize its Spring components.
        SpringApplication.run(VAappApplication.class, args);
    }
}