package com.logistics.delivery.domain.exception;

public class ZoneNotFoundException extends RuntimeException {
    public ZoneNotFoundException(String zoneId) {
        super("Zone not found: " + zoneId);
    }
}
