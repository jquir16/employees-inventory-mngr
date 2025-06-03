package com.katabdb.employee.onboarding.mngr.exception;

public class InvalidUserFormatException extends RuntimeException {
    public InvalidUserFormatException(String message) {
        super(message);
    }
}
