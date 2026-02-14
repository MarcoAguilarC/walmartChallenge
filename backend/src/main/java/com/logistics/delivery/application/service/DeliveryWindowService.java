package com.logistics.delivery.application.service;

import com.logistics.delivery.domain.exception.ZoneNotActiveException;
import com.logistics.delivery.domain.exception.ZoneNotFoundException;
import com.logistics.delivery.domain.model.DeliveryWindow;
import com.logistics.delivery.domain.model.GeographicZone;
import com.logistics.delivery.domain.model.ReservationStatus;
import com.logistics.delivery.infrastructure.adapters.persistence.DeliveryWindowRepository;
import com.logistics.delivery.infrastructure.adapters.persistence.GeographicZoneRepository;
import com.logistics.delivery.infrastructure.adapters.persistence.ReservationRepository;
import com.logistics.delivery.infrastructure.adapters.rest.dto.AvailableDateDTO;
import com.logistics.delivery.infrastructure.adapters.rest.dto.WindowAvailabilityResponse;
import com.logistics.delivery.infrastructure.adapters.rest.dto.WindowSlotDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryWindowService {

    private final DeliveryWindowRepository windowRepo;
    private final GeographicZoneRepository zoneRepository;
    private final ReservationRepository reservationRepo;

    @Cacheable(value = "window-availability", key = "#zoneId + '-' + #startDate + '-' + #days")
    public WindowAvailabilityResponse getAvailableWindows(
            String zoneId,
            LocalDate startDate,
            int days) {
        log.info("Fetching availability for zone {} from {} for {} days", zoneId, startDate, days);

        GeographicZone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ZoneNotFoundException(zoneId));

        if (!zone.isActive()) {
            throw new ZoneNotActiveException(zoneId);
        }

        List<DeliveryWindow> windows = windowRepo.findByDateBetweenAndActiveTrue(
                startDate,
                startDate.plusDays(days - 1));

        log.info("Found {} total active windows in range", windows.size());

        Map<LocalDate, List<WindowSlotDTO>> availabilityByDate = windows.stream()
                .filter(window -> {
                    boolean hasZone = window.getCapacityByZone().containsKey(zoneId);
                    if (!hasZone) {
                        log.warn("Window {} does not cover zone {}", window.getId(), zoneId);
                    }
                    return hasZone;
                })
                .collect(Collectors.groupingBy(
                        DeliveryWindow::getDate,
                        Collectors.mapping(
                                window -> buildWindowSlotDTO(window, zoneId),
                                Collectors.toList())));

        return WindowAvailabilityResponse.builder()
                .availableDates(buildAvailableDates(availabilityByDate, startDate, days))
                .build();
    }

    @CacheEvict(value = "window-availability", allEntries = true)
    public void invalidateAvailabilityCache(String windowId) {
        log.debug("Cache invalidated for window: {}", windowId);
    }

    private List<AvailableDateDTO> buildAvailableDates(Map<LocalDate, List<WindowSlotDTO>> availabilityByDate,
            LocalDate startDate, int days) {
        return startDate.datesUntil(startDate.plusDays(days))
                .map(date -> {
                    List<WindowSlotDTO> slots = availabilityByDate.getOrDefault(date, List.of());
                    return AvailableDateDTO.builder()
                            .date(date)
                            .dayName(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("es", "CL")))
                            .dayNumber(date.getDayOfMonth() + "/" + date.getMonthValue())
                            .windows(slots)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private WindowSlotDTO buildWindowSlotDTO(DeliveryWindow window, String zoneId) {
        int currentReservations = reservationRepo
                .countByWindowIdAndZoneIdAndStatusIn(
                        window.getId(),
                        zoneId,
                        List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED));

        Integer zoneCapacity = window.getCapacityByZone().get(zoneId);
        int remainingCapacity = zoneCapacity != null ? Math.max(0, zoneCapacity - currentReservations) : 0;

        return WindowSlotDTO.builder()
                .id(window.getId())
                .startTime(window.getStartTime())
                .endTime(window.getEndTime())
                .display(formatTimeRange(window.getStartTime(), window.getEndTime()))
                .price(window.getPrice())
                .available(remainingCapacity > 0)
                .remainingCapacity(remainingCapacity)
                .build();
    }

    private String formatTimeRange(LocalTime start, LocalTime end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mma").withLocale(Locale.US);
        return start.format(formatter).toLowerCase() + "-" + end.format(formatter).toLowerCase();
    }
}
