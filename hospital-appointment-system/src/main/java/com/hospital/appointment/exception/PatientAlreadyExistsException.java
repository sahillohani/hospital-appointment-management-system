package com.hospital.appointment.exception;

public class PatientAlreadyExistsException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PatientAlreadyExistsException(String message) {
        super(message);
    }
}