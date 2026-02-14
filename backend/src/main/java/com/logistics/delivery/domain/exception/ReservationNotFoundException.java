package com.logistics.delivery.domain.exception;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(String id) {
        super("Reserva no encontrada: " + id);
    }
}
