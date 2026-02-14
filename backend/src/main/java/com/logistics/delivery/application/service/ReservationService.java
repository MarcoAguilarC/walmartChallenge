package com.logistics.delivery.application.service;

import com.logistics.delivery.domain.exception.*;
import com.logistics.delivery.domain.model.*;
import com.logistics.delivery.infrastructure.adapters.persistence.DeliveryWindowRepository;
import com.logistics.delivery.infrastructure.adapters.persistence.GeographicZoneRepository;
import com.logistics.delivery.infrastructure.adapters.persistence.ReservationRepository;
import com.logistics.delivery.infrastructure.adapters.rest.dto.ReservationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.OptimisticLockException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private static final String LOCK_KEY_PREFIX = "lock:window:";
    private static final long LOCK_WAIT_TIME = 3;
    private static final long LOCK_LEASE_TIME = 10;

    private final RedissonClient redisson;
    private final ReservationRepository reservationRepo;
    private final DeliveryWindowRepository windowRepo;
    private final GeographicZoneRepository zoneRepository;
    private final DeliveryWindowService windowService;

    public Reservation createReservation(ReservationRequest request) {
        String lockKey = buildLockKey(request.getWindowId(), request.getZoneId());
        RLock lock = redisson.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);

            if (!acquired) {
                throw new ConcurrentAccessException("Window currently busy. Please retry.");
            }

            log.info("Lock acquired for window={}, zone={}",
                    request.getWindowId(), request.getZoneId());

            return createReservationWithRetry(request);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operation interrupted", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Retryable(retryFor = {
            OptimisticLockException.class }, maxAttempts = 3, backoff = @Backoff(delay = 100, multiplier = 2))
    @Transactional
    public Reservation createReservationWithRetry(ReservationRequest request) {
        GeographicZone zone = zoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new ZoneNotFoundException(request.getZoneId()));

        if (!zone.isActive()) {
            throw new ZoneNotActiveException(request.getZoneId());
        }

        DeliveryWindow window = windowRepo.findById(request.getWindowId())
                .orElseThrow(() -> new WindowNotFoundException(request.getWindowId()));

        if (!window.getCapacityByZone().containsKey(request.getZoneId())) {
            throw new ZoneNotCoveredException("Window does not cover zone " + request.getZoneId());
        }

        int currentReservations = countActiveReservations(request.getWindowId(), request.getZoneId());

        if (!window.hasCapacityForZone(request.getZoneId(), currentReservations)) {
            throw new WindowFullException(window.getId());
        }

        Reservation reservation = Reservation.builder()
                .windowId(request.getWindowId())
                .userId(request.getUserId())
                .zoneId(request.getZoneId())
                .price(window.getPrice())
                .status(ReservationStatus.CONFIRMED)
                .build();

        Reservation saved = reservationRepo.save(reservation);
        windowService.invalidateAvailabilityCache(request.getWindowId());

        log.info("Reservation created: id={}, window={}", saved.getId(), request.getWindowId());
        return saved;
    }

    private String buildLockKey(String windowId, String zoneId) {
        return LOCK_KEY_PREFIX + windowId + ":" + zoneId;
    }

    private int countActiveReservations(String windowId, String zoneId) {
        return reservationRepo.countByWindowIdAndZoneIdAndStatusIn(
                windowId,
                zoneId,
                List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED));
    }
}
