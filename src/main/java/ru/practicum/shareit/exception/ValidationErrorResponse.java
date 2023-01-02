package ru.practicum.shareit.exception;

import lombok.Data;

@Data
public class ValidationErrorResponse {
    private final String error;
}
