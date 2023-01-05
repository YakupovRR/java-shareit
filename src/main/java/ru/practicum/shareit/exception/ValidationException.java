package ru.practicum.shareit.exception;

public class ValidationException extends RuntimeException {
    private String error;

    public ValidationException(final String error) {
        super(error);
        this.error = error;
    }
}

