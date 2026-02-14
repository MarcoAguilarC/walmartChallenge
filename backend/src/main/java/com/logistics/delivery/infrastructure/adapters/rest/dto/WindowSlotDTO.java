package com.logistics.delivery.infrastructure.adapters.rest.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WindowSlotDTO {
    private String id;
    private LocalTime startTime;
    private LocalTime endTime;
    private String display;
    private BigDecimal price;
    private boolean available;
    private int remainingCapacity;
}
