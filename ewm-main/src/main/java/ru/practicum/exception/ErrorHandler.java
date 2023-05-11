package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.dto.ApiError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ApiError> handleEntityNotFoundException(EntityNotFoundException e) {
        return apiErrorResponseEntity(e.getMessage(), e,
                "The required object was not found.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleDataValidationException(FieldValidationException e) {
        return apiErrorResponseEntity(e.getMessage(), e,
                "For the requested operation the conditions are not met.", HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleCheckStatusException(CheckStatusException e) {
        return apiErrorResponseEntity(e.getMessage(), e,
                "For the requested operation the conditions are not met.", HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return apiErrorResponseEntity(e.getMessage(), e,
                "Incorrectly made request.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException e) {
        return apiErrorResponseEntity(e.getMessage(), e,
                "Integrity constraint has been violated.", HttpStatus.CONFLICT);
    }

    private ResponseEntity<ApiError> apiErrorResponseEntity(String message, Throwable e,
                                                            String reason, HttpStatus httpStatus) {
        log.warn(message);
        List<String> errors = new ArrayList<>();
        errors.add(message);
        ApiError error = new ApiError(errors,
                message,
                reason,
                httpStatus.toString(),
                LocalDateTime.now());
        return new ResponseEntity<>(error, httpStatus);
    }
}