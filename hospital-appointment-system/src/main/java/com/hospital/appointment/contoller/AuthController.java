package com.hospital.appointment.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hospital.appointment.dto.LoginRequest;
import com.hospital.appointment.dto.LoginResponse;
import com.hospital.appointment.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request) {

        return userService.login(
                request.getUsername(),
                request.getPassword());
    }
}