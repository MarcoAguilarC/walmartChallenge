package com.logistics.delivery.domain.exception;

public class WindowFullException extends RuntimeException {
    public WindowFullException(String windowId) {
        super("Delivery window " + windowId + " is full.");
    }
}
