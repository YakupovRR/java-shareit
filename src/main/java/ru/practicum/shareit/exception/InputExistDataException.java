package ru.practicum.shareit.exception;

public class InputExistDataException extends IllegalArgumentException {
    public InputExistDataException(final String message) {
        super(message);
    }
}
