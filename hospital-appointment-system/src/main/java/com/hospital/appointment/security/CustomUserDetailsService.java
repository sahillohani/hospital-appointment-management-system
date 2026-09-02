package com.hospital.appointment.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hospital.appointment.entity.User;
import com.hospital.appointment.repository.UserRepository;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(
            String username)
            throws UsernameNotFoundException {

        // Hardcoded Admin
        if ("admin".equals(username)) {

            return new org.springframework.security.core.userdetails.User(
                    "admin",
                    "{noop}admin123",
                    Collections.singletonList(
                            new SimpleGrantedAuthority(
                                    "ROLE_ADMIN")));
        }

        // Doctor/Patient from Database
        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(
                        new SimpleGrantedAuthority(
                                "ROLE_" + user.getRole().name())));
    }
}