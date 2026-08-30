package com.hospital.appointment.exception;

public class InvalidAppointmentSlotException extends RuntimeException {

    public InvalidAppointmentSlotException(String message) {
        super(message);
    }
}