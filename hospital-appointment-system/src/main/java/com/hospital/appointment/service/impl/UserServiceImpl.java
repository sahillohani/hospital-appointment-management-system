package com.hospital.appointment.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hospital.appointment.dto.LoginResponse;
import com.hospital.appointment.entity.User;
import com.hospital.appointment.exception.InvalidCredentialsException;
import com.hospital.appointment.exception.UserNotFoundException;
import com.hospital.appointment.repository.UserRepository;
import com.hospital.appointment.security.JwtUtil;
import com.hospital.appointment.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginResponse login(String username,
                               String password) {

        if ("admin".equals(username)
                && "admin123".equals(password)) {

            String token =
                    jwtUtil.generateToken(
                            "admin",
                            "ADMIN");

            return new LoginResponse(
                    "Login Successful",
                    "ADMIN",
                    token);
        }

        User user =
                userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new UserNotFoundException(
                                        "User not found"));

        if (!user.getPassword().equals(password)) {

            throw new InvalidCredentialsException(
                    "Invalid Password");
        }

        String token =
                jwtUtil.generateToken(
                        user.getUsername(),
                        user.getRole().name());

        return new LoginResponse(
                "Login Successful",
                user.getRole().name(),
                token);
    }
}