package com.logistics.delivery.infrastructure.adapters.persistence;

import com.logistics.delivery.domain.model.Reservation;
import com.logistics.delivery.domain.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    int countByWindowIdAndZoneIdAndStatusIn(String windowId, String zoneId, Collection<ReservationStatus> statuses);

    int countByWindowIdAndStatus(String windowId, ReservationStatus status);

}
