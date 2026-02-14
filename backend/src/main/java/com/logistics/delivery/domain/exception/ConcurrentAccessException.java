package com.logistics.delivery.domain.exception;

public class ConcurrentAccessException extends RuntimeException {
    public ConcurrentAccessException(String message) {
        super(message);
    }
}
