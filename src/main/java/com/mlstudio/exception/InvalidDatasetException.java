package com.mlstudio.exception;

public class InvalidDatasetException extends Exception {
    public InvalidDatasetException(String message) {
        super(message);
    }
    
    public InvalidDatasetException(String message, Throwable cause) {
        super(message, cause);
    }
}
