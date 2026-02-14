package com.logistics.delivery.infrastructure.adapters.rest;

import com.logistics.delivery.application.service.DeliveryWindowService;
import com.logistics.delivery.application.service.ReservationService;
import com.logistics.delivery.domain.model.GeographicZone;
import com.logistics.delivery.domain.model.Reservation;
import com.logistics.delivery.infrastructure.adapters.persistence.GeographicZoneRepository;
import com.logistics.delivery.infrastructure.adapters.rest.dto.ReservationRequest;
import com.logistics.delivery.infrastructure.adapters.rest.dto.ReservationResponse;
import com.logistics.delivery.infrastructure.adapters.rest.dto.ZoneResponseDTO;
import com.logistics.delivery.infrastructure.adapters.rest.dto.WindowAvailabilityResponse;
import com.logistics.delivery.infrastructure.adapters.rest.mapper.DeliveryMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/delivery")
@Validated
@RequiredArgsConstructor
@Slf4j
public class DeliveryController {

    private final DeliveryWindowService windowService;
    private final ReservationService reservationService;
    private final GeographicZoneRepository zoneRepo;
    private final DeliveryMapper deliveryMapper;

    @GetMapping("/zones")
    public ResponseEntity<List<ZoneResponseDTO>> getAvailableZones() {
        return ResponseEntity.ok(deliveryMapper.toZoneResponseList(zoneRepo.findByActiveTrue()));
    }

    @GetMapping("/windows")
    public ResponseEntity<WindowAvailabilityResponse> getAvailableWindows(
            @RequestParam @NotBlank String zoneId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "7") @Min(1) @Max(14) int days) {

        WindowAvailabilityResponse response = windowService.getAvailableWindows(
                zoneId, startDate, days);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody @Valid ReservationRequest request) {

        log.info("Creating reservation request for window: {}", request.getWindowId());
        Reservation reservation = reservationService.createReservation(request);
        ReservationResponse response = deliveryMapper.toReservationResponse(reservation);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
