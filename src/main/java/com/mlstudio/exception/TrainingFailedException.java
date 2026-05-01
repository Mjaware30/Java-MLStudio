package com.mlstudio.exception;

public class TrainingFailedException extends RuntimeException {
    public TrainingFailedException(String message) {
        super(message);
    }
    
    public TrainingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
