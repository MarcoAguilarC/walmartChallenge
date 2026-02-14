package com.logistics.delivery.domain.exception;

public class ZoneFullException extends RuntimeException {
    public ZoneFullException(String zoneId, String windowId) {
        super("Zone " + zoneId + " in delivery window " + windowId + " is full.");
    }
}
