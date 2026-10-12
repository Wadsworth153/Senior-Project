package com.gcu.va.config;

// Import the annotation used to identify this class as a configuration class.
import org.springframework.context.annotation.Configuration;

// Import the registry used to register custom formatters and converters.
import org.springframework.format.FormatterRegistry;

// Import the interface for customizing Spring MVC configuration.
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Import the registrar used to configure date and time formatters.
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;

/**
 * Configures date and time formatting for the application's web layer.
 *
 * This configuration registers ISO-compatible date and time formatters
 * with Spring MVC to provide consistent formatting and conversion of
 * date values submitted through web forms.
 */
@Configuration
public class DateConfig implements WebMvcConfigurer {

    /**
     * Registers date and time formatters with Spring MVC.
     *
     * The formatter registrar is configured to use ISO formatting before
     * registering its formatters with Spring's formatter registry.
     *
     * @param registry the registry used to register formatters and converters
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {

        // Create a registrar for date and time formatting.
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();

        // Configure the registrar to use ISO date and time formats.
        registrar.setUseIsoFormat(true);

        // Register the configured formatters with Spring MVC.
        registrar.registerFormatters(registry);
    }
}