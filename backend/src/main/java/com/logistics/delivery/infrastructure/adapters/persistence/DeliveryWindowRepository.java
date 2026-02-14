package com.logistics.delivery.infrastructure.adapters.persistence;

import com.logistics.delivery.domain.model.DeliveryWindow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DeliveryWindowRepository extends JpaRepository<DeliveryWindow, String> {
    List<DeliveryWindow> findByDateBetweenAndActiveTrue(LocalDate start, LocalDate end);
}
