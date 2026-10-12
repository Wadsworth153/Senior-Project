package com.gcu.va.config;

// Import the annotation used to identify this class as a Spring configuration class.
import org.springframework.context.annotation.Configuration;

// Import the registry used to register formatters and converters.
import org.springframework.format.FormatterRegistry;

// Import the interface for customizing Spring MVC configuration.
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Provides a configuration class for Spring MVC web functionality.
 *
 * This class implements WebMvcConfigurer to allow customization of
 * Spring MVC behavior. It currently does not override any configuration
 * methods because no additional MVC customization is defined here.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Additional Spring MVC configuration can be implemented here if needed.
}