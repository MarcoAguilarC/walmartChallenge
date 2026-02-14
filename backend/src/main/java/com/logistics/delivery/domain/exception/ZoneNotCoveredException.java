package com.logistics.delivery.domain.exception;

public class ZoneNotCoveredException extends RuntimeException {
    public ZoneNotCoveredException(String message) {
        super(message);
    }
}
