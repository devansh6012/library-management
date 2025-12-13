package com.example.librarymanagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;  // ADD THIS
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;            // ADD THIS

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,  // ADD THIS
                          JwtAccessDeniedHandler jwtAccessDeniedHandler) {          // ADD THIS
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;  // ADD THIS
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;            // ADD THIS
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - Auth
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Public endpoints - H2 Console
                        .requestMatchers("/h2-console/**").permitAll()

                        // Public endpoints - Library (GET only)
                        .requestMatchers(HttpMethod.GET, "/api/v1/library/books").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/library/books/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/library/info").permitAll()

                        // Admin only endpoints
                        .requestMatchers(HttpMethod.POST, "/api/v1/library/books").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/library/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/library/books/**").hasRole("ADMIN")

                        // Admin and Librarian endpoints
                        .requestMatchers("/api/v1/members/**").hasAnyRole("ADMIN", "LIBRARIAN")

                        // Authenticated endpoints - Borrowing
                        .requestMatchers(HttpMethod.POST, "/api/v1/library/books/*/borrow").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/library/books/*/return").authenticated()

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)  // ADD THIS - for 401
                        .accessDeniedHandler(jwtAccessDeniedHandler)            // ADD THIS - for 403
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // H2 Console specific
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}