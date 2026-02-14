package com.logistics.delivery.application.service;

import com.logistics.delivery.domain.model.DeliveryWindow;
import com.logistics.delivery.domain.model.GeographicZone;
import com.logistics.delivery.infrastructure.adapters.persistence.DeliveryWindowRepository;
import com.logistics.delivery.infrastructure.adapters.persistence.GeographicZoneRepository;
import com.logistics.delivery.infrastructure.adapters.persistence.ReservationRepository;
import com.logistics.delivery.infrastructure.adapters.rest.dto.ReservationRequest;
import com.logistics.delivery.domain.model.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.embedded.RedisServer;
import org.junit.jupiter.api.AfterAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookingConcurrencyIntegrationTest {

    private static RedisServer redisServer;

    static {
        try {
            redisServer = RedisServer.builder()
                    .port(6370)
                    .setting("maxheap 200m")
                    .build();
            redisServer.start();
        } catch (Exception e) {
            System.err.println("Failed to start embedded Redis: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDownRedis() {
        if (redisServer != null) {
            try {
                redisServer.stop();
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private DeliveryWindowRepository windowRepository;

    @Autowired
    private GeographicZoneRepository zoneRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private String windowId = "test-window-concurrent";
    private String zoneId = "zone-1";

    @BeforeEach
    void setup() {
        reservationRepository.deleteAll();
        windowRepository.deleteAll();
        zoneRepository.deleteAll();

        GeographicZone zone = GeographicZone.builder()
                .id(zoneId)
                .active(true)
                .build();
        zoneRepository.save(zone);

        Map<String, Integer> capacityByZone = new HashMap<>();
        capacityByZone.put(zoneId, 1);

        DeliveryWindow window = DeliveryWindow.builder()
                .id(windowId)
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(12, 0))
                .capacityTotal(10)
                .capacityByZone(capacityByZone)
                .price(new BigDecimal("1000"))
                .active(true)
                .build();

        windowRepository.save(window);
    }

    @Test
    void testConcurrentBookingPrecludesDoubleBooking() throws InterruptedException {
        int threads = 5;
        ExecutorService service = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            final String userId = "user-" + i;
            service.submit(() -> {
                try {
                    startLatch.await();
                    ReservationRequest request = ReservationRequest.builder()
                            .windowId(windowId)
                            .zoneId(zoneId)
                            .userId(userId)
                            .build();
                    reservationService.createReservation(request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await(10, TimeUnit.SECONDS);

        assertEquals(1, successCount.get(), "Only one booking should succeed");
        assertEquals(threads - 1, failureCount.get(), "The other threads should have failed");

        long activeReservations = reservationRepository.countByWindowIdAndZoneIdAndStatusIn(
                windowId, zoneId, List.of(ReservationStatus.CONFIRMED));
        assertEquals(1, activeReservations, "Final reservation count must be 1");

        service.shutdown();
    }
}
