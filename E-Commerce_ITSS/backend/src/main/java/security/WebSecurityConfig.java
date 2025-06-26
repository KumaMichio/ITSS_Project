package security;

// Removed unused Autowired
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    // Removed unused CustomUserDetailsService

    private JwtAuthEntryPoint jwtAuthEntryPoint;

    public WebSecurityConfig(JwtAuthEntryPoint jwtAuthEntryPoint) {
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .exceptionHandling((exception) -> exception.authenticationEntryPoint(jwtAuthEntryPoint)
                        .accessDeniedPage("/error/access-denied"))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        // Product endpoints security configuration
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAnyAuthority("admin", "ROLE_admin") // Restrict
                                                                                                                       // DELETE
                                                                                                                       // to
                                                                                                                       // admin
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAnyAuthority("admin", "ROLE_admin") // Restrict
                                                                                                                     // POST
                                                                                                                     // to
                                                                                                                     // admin
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyAuthority("admin", "ROLE_admin") // Restrict
                                                                                                                    // PUT
                                                                                                                    // to
                                                                                                                    // admin
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll() // Allow GET for all
                        .requestMatchers("/api/payment/**").permitAll() // Allow all payment endpoints
                        .requestMatchers(HttpMethod.POST, "/api/payment/**").permitAll() // Allow POST to payment
                                                                                         // endpoints
                        .requestMatchers("/vnpay-demo.html").permitAll() // Allow VNPay demo page
                        .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll() // Allow static
                                                                                                      // resources
                        .requestMatchers(HttpMethod.POST, "/api/user").hasAnyAuthority("admin", "ROLE_admin") // Support
                                                                                                              // both
                                                                                                              // formats
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll() // Allow all GET requests to API
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    //
    // public UserDetailsService users(){
    // UserDetails admin = (UserDetails) User.builder().role("admin").build();
    // UserDetails user = (UserDetails) User.builder().role("user").build();
    // return new InMemoryUserDetailsManager(admin, user);
    // }
    //

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}