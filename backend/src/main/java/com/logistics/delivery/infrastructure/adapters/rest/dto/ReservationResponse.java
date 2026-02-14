package com.logistics.delivery.infrastructure.adapters.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.logistics.delivery.domain.model.ReservationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private String id;
    private String windowId;
    private String zoneId;
    private BigDecimal price;
    private ReservationStatus status;
}
