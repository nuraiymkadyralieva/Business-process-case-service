package org.example.businessprocessservice.exception;

public class ForbiddenStatusTransitionException extends RuntimeException {

    public ForbiddenStatusTransitionException(String message) {
        super(message);
    }
}