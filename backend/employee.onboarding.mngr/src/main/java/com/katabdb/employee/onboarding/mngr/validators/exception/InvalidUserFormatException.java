package com.katabdb.employee.onboarding.mngr.validators.exception;

public class InvalidUserFormatException extends RuntimeException {
    public InvalidUserFormatException(String message) {
        super(message);
    }
}
