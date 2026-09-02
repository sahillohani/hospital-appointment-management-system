package com.hospital.appointment.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hospital.appointment.security.CustomAccessDeniedHandler;
import com.hospital.appointment.security.JwtFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;
    
    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {

        UserDetails admin =
                User.withUsername("admin")
                        .password("{noop}admin123")
                        .roles("ADMIN")
                        .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
        .exceptionHandling(exception ->
        exception.accessDeniedHandler(
                customAccessDeniedHandler))
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // ==========================
                        // PUBLIC ENDPOINTS
                        // ==========================
                        .requestMatchers(
                                "/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**")
                        .permitAll()

                        // ==========================
                        // PUBLIC REGISTRATION
                        // ==========================
                        .requestMatchers(
                                HttpMethod.POST,
                                "/patients")
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/doctors")
                        .permitAll()

                        // ==========================
                        // PATIENT ACCESS
                        // ==========================

                        // Patient CANNOT view all patients
                        .requestMatchers(
                                HttpMethod.GET,
                                "/patients")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/patients/**")
                        .hasAnyRole("PATIENT", "ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/patients/**")
                        .hasAnyRole("PATIENT", "ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/patients/**")
                        .hasAnyRole("PATIENT", "ADMIN")

                        // Patient CAN view doctor list
                        .requestMatchers(
                                HttpMethod.GET,
                                "/doctors")
                        .hasAnyRole("PATIENT", "ADMIN")

                        // Patient can book appointment
                        .requestMatchers(
                                HttpMethod.POST,
                                "/appointments")
                        .hasAnyRole("PATIENT", "ADMIN")

                        .requestMatchers(
                                "/appointments/patient/**")
                        .hasAnyRole("PATIENT", "ADMIN")

                        // ==========================
                        // DOCTOR ACCESS
                        // ==========================

                        // Doctor CANNOT view all doctors
                        .requestMatchers(
                                HttpMethod.GET,
                                "/doctors/**")
                        .hasAnyRole("DOCTOR", "ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/doctors/**")
                        .hasAnyRole("DOCTOR", "ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/doctors/**")
                        .hasAnyRole("DOCTOR", "ADMIN")

                        .requestMatchers(
                                "/appointments/doctor/**")
                        .hasAnyRole("DOCTOR", "ADMIN")

                        // ==========================
                        // ADMIN ONLY
                        // ==========================

                        .requestMatchers("/appointments/**")
                        .hasRole("ADMIN")

                        .anyRequest()
                        .authenticated())

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}