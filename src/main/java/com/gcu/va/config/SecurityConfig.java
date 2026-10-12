package com.gcu.va.config;

// Import the annotations used to define Spring configuration and beans.
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Import HttpSecurity to configure web application security.
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

// Import the security filter chain that processes incoming HTTP requests.
import org.springframework.security.web.SecurityFilterChain;

// Import BCrypt for password encoding.
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Import the interface used to encode and verify passwords.
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configures authentication and authorization for the VA Benefits and
 * Eligibility Tracking System.
 *
 * This class defines which application routes are publicly accessible,
 * which routes require an authenticated user, and which routes require
 * specific user roles.
 *
 * It also configures the login page, logout behavior, and password encoder.
 */
@Configuration
public class SecurityConfig {

    /**
     * Creates the security filter chain for incoming HTTP requests.
     *
     * Public registration, login, CSS, and JavaScript resources are
     * accessible without authentication. Administrative routes require
     * the ADMIN role, while the profile and new-claim routes require
     * the VETERAN role. Other routes require an authenticated user.
     *
     * The method also configures the custom login page and logout behavior.
     *
     * @param http the object used to configure HTTP security
     * @return the configured security filter chain
     * @throws Exception if an error occurs while configuring security
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Configure authorization rules for application requests.
        http
            .authorizeHttpRequests(auth -> auth

                // Allow unauthenticated access to registration, login,
                // and the application's CSS and JavaScript resources.
                .requestMatchers(
                    "/register",
                    "/login",
                    "/css/**",
                    "/js/**"
                ).permitAll()

                // Restrict administrative pages and the veteran records
                // management page to users with the ADMIN role.
                .requestMatchers(
                    "/admin",
                    "/admin/**",
                    "/veterans-page"
                ).hasRole("ADMIN")

                // Restrict the profile page to users with the VETERAN role.
                .requestMatchers("/my-profile").hasRole("VETERAN")

                // Require authentication before allowing access to the home page.
                .requestMatchers("/home").authenticated()

                // Restrict claim creation to users with the VETERAN role.
                .requestMatchers("/claims/new").hasRole("VETERAN")

                // Require authentication for all other requests.
                .anyRequest().authenticated()
            )

            // Configure the custom login page and successful login destination.
            .formLogin(form -> form
                // Use the application's login page.
                .loginPage("/login")

                // Redirect users to /home after successful authentication.
                // The true argument forces this destination.
                .defaultSuccessUrl("/home", true)

                // Allow unauthenticated users to access the login process.
                .permitAll()
            )

            // Configure logout behavior.
            .logout(logout -> logout

                // Redirect users to the login page after logging out.
                .logoutSuccessUrl("/login?logout")

                // Allow unauthenticated users to access the logout endpoint.
                .permitAll()
            );

        // Build and return the configured security filter chain.
        return http.build();
    }

    /**
     * Creates the password encoder used by the application.
     *
     * BCrypt provides a one-way password hashing mechanism that allows
     * stored password hashes to be checked during authentication without
     * storing the original passwords in plain text.
     *
     * @return a BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        // Create and return the BCrypt password encoder.
        return new BCryptPasswordEncoder();
    }
}