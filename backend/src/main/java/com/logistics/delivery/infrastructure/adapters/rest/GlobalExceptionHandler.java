package com.logistics.delivery.infrastructure.adapters.rest;

import com.logistics.delivery.domain.exception.*;
import jakarta.validation.ConstraintViolationException;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(WindowFullException.class)
        public ResponseEntity<ErrorResponse> handleWindowFull(WindowFullException e) {
                return buildErrorResponse(HttpStatus.CONFLICT, "WINDOW_FULL", e.getMessage());
        }

        @ExceptionHandler(ConcurrentAccessException.class)
        public ResponseEntity<ErrorResponse> handleConcurrentAccess(ConcurrentAccessException e) {
                return buildErrorResponse(HttpStatus.TOO_MANY_REQUESTS, "CONCURRENT_ACCESS", e.getMessage());
        }

        @ExceptionHandler({ ZoneNotFoundException.class, WindowNotFoundException.class })
        public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException e) {
                String code = e instanceof ZoneNotFoundException ? "ZONE_NOT_FOUND" : "WINDOW_NOT_FOUND";
                return buildErrorResponse(HttpStatus.NOT_FOUND, code, e.getMessage());
        }

        @ExceptionHandler(ZoneNotActiveException.class)
        public ResponseEntity<ErrorResponse> handleZoneNotActive(ZoneNotActiveException e) {
                return buildErrorResponse(HttpStatus.BAD_REQUEST, "ZONE_INACTIVE", e.getMessage());
        }

        @ExceptionHandler(ZoneNotCoveredException.class)
        public ResponseEntity<ErrorResponse> handleZoneNotCovered(ZoneNotCoveredException e) {
                return buildErrorResponse(HttpStatus.BAD_REQUEST, "ZONE_NOT_COVERED", e.getMessage());
        }

        @ExceptionHandler(ReservationNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleReservationNotFound(ReservationNotFoundException e) {
                return buildErrorResponse(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND", e.getMessage());
        }

        @ExceptionHandler({ MethodArgumentNotValidException.class, ConstraintViolationException.class })
        public ResponseEntity<ErrorResponse> handleValidation(Exception e) {
                return buildErrorResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Invalid parameters");
        }

        @ExceptionHandler(IllegalStateException.class)
        public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
                return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_STATE", e.getMessage());
        }

        private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String code, String message) {
                ErrorResponse error = ErrorResponse.builder()
                                .type("/errors/" + code.toLowerCase().replace("_", "-"))
                                .title(code)
                                .status(status.value())
                                .detail(message)
                                .timestamp(LocalDateTime.now())
                                .build();
                return new ResponseEntity<>(error, status);
        }

        @Data
        @Builder
        static class ErrorResponse {
                private String type;
                private String title;
                private int status;
                private String detail;
                private LocalDateTime timestamp;
        }
}
