package com.hospital.appointment.exception;

public class UnauthorizedDoctorAccessException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedDoctorAccessException(
            String message) {

        super(message);
    }
}