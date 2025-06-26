package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /*
     * Commented out to rely solely on the CorsConfigurationSource bean for Spring
     * Security
     * 
     * @Override
     * public void addCorsMappings(CorsRegistry registry) {
     * registry.addMapping("/**")
     * .allowedOrigins("http://localhost:5173", "http://localhost:3000",
     * "http://localhost:5174")
     * .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
     * .allowedHeaders("*")
     * .allowCredentials(true);
     * }
     */ @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow specific origins including the React app on port 5173 and other common
        // ports
        configuration.setAllowedOriginPatterns(
                Arrays.asList("http://localhost:5173", "http://localhost:3000", "http://localhost:8080",
                        "http://localhost:*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        // Allow all headers for debugging and payment processing
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "Access-Control-Allow-Origin",
                "Access-Control-Allow-Headers", "Access-Control-Allow-Methods")); // Headers client needs to
        // access
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // How long the results of a preflight request can be cached

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply to all endpoints including payment
        return source;
    }
}
