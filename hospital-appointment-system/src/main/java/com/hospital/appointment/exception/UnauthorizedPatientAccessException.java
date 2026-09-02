package com.hospital.appointment.exception;

public class UnauthorizedPatientAccessException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedPatientAccessException(
            String message) {

        super(message);
    }
}