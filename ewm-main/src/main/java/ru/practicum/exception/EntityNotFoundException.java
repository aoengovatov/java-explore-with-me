package ru.practicum.exception;

public class EntityNotFoundException extends RuntimeException {

    public <T> EntityNotFoundException(Class<T> entityClass, Long entityId) {
        super(String.format("Entity '%s' with id '%d' was not found", entityClass.getName(), entityId));
    }

    public <T> EntityNotFoundException(Class<T> entityClass) {
        super(String.format("Entity '%s' was not found", entityClass.getName()));
    }
}
