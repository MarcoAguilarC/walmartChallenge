package com.logistics.delivery.domain.exception;

public class WindowNotFoundException extends RuntimeException {
    public WindowNotFoundException(String windowId) {
        super("Window not found: " + windowId);
    }
}
