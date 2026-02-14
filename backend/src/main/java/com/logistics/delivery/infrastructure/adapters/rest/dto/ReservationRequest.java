package com.logistics.delivery.infrastructure.adapters.rest.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {

    @NotBlank
    private String windowId;

    @NotBlank
    private String zoneId;

    @NotBlank
    private String userId;
}
