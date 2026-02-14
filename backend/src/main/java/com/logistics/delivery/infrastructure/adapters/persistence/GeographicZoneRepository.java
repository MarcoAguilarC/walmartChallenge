package com.logistics.delivery.infrastructure.adapters.persistence;

import com.logistics.delivery.domain.model.GeographicZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeographicZoneRepository extends JpaRepository<GeographicZone, String> {
    List<GeographicZone> findByActiveTrue();
}
