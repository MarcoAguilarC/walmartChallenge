package com.logistics.delivery.domain.exception;

public class ZoneNotActiveException extends RuntimeException {
    public ZoneNotActiveException(String zoneId) {
        super("Zone is not active: " + zoneId);
    }
}
