package com.hospital.appointment.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hospital.appointment.security.JwtFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

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
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // Public APIs
                        .requestMatchers(
                                "/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**")
                        .permitAll()

                        // Patient Controller
                        .requestMatchers("/patients/**")
                        .hasAnyRole("PATIENT", "ADMIN")

                        // Doctor Controller
                        .requestMatchers("/doctors/**")
                        .hasAnyRole("DOCTOR", "ADMIN")

                        // Patient Appointment APIs
                        .requestMatchers("/appointments/patient/**")
                        .hasAnyRole("PATIENT", "ADMIN")

                        // Doctor Appointment APIs
                        .requestMatchers("/appointments/doctor/**")
                        .hasAnyRole("DOCTOR", "ADMIN")

                        // All Other Appointment APIs
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