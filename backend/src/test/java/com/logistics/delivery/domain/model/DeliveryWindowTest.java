package com.logistics.delivery.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DeliveryWindowTest {

    private DeliveryWindow window;
    private final String zoneId = "zone-1";

    @BeforeEach
    void setUp() {
        Map<String, Integer> capacities = new HashMap<>();
        capacities.put(zoneId, 2);

        window = DeliveryWindow.builder()
                .id("test-window")
                .date(LocalDate.now())
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .price(new BigDecimal("100.00"))
                .capacityTotal(5)
                .capacityByZone(capacities)
                .active(true)
                .build();
    }

    @Test
    void hasCapacityForZone_ShouldReturnTrue_WhenUnderCapacity() {
        assertThat(window.hasCapacityForZone(zoneId, 0)).isTrue();
        assertThat(window.hasCapacityForZone(zoneId, 1)).isTrue();
    }

    @Test
    void hasCapacityForZone_ShouldReturnFalse_WhenAtCapacity() {
        assertThat(window.hasCapacityForZone(zoneId, 2)).isFalse();
    }

    @Test
    void hasCapacityForZone_ShouldReturnFalse_WhenOverCapacity() {
        assertThat(window.hasCapacityForZone(zoneId, 3)).isFalse();
    }

    @Test
    void hasCapacityForZone_ShouldReturnFalse_WhenZoneNotCovered() {
        assertThat(window.hasCapacityForZone("unknown-zone", 0)).isFalse();
    }

    @Test
    void hasCapacityForZone_ShouldReturnFalse_WhenCapacitiesMapIsNull() {
        window.setCapacityByZone(null);
        assertThat(window.hasCapacityForZone(zoneId, 0)).isFalse();
    }
}
