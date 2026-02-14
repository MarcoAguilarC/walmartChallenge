package com.logistics.delivery.infrastructure.adapters.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableDateDTO {
    private LocalDate date;
    private String dayName;
    private String dayNumber;
    private List<WindowSlotDTO> windows;
}
