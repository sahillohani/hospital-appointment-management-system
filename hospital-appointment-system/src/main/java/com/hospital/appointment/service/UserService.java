package com.hospital.appointment.service;

import com.hospital.appointment.dto.LoginResponse;

public interface UserService {
	LoginResponse login(String username,String password);

}