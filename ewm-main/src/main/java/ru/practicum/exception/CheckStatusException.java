package ru.practicum.exception;

public class CheckStatusException extends RuntimeException {
    public CheckStatusException(String message) {
        super(message);
    }
}