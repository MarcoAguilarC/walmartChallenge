package com.logistics.delivery.infrastructure.adapters.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.delivery.domain.model.DeliveryWindow;
import com.logistics.delivery.domain.model.GeographicZone;
import com.logistics.delivery.infrastructure.adapters.persistence.DeliveryWindowRepository;
import com.logistics.delivery.infrastructure.adapters.persistence.GeographicZoneRepository;
import com.logistics.delivery.infrastructure.adapters.persistence.ReservationRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataInitializer.class);

    private final DeliveryWindowRepository windowRepo;
    private final GeographicZoneRepository zoneRepo;
    private final JdbcTemplate jdbcTemplate;
    private final ReservationRepository reservationRepo;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    @Override
    public void run(String... args) {
        log.info("Checking database schema and data state...");

        // Migracion para compatibilidad de esquema
        try {
            jdbcTemplate.execute("ALTER TABLE reservations DROP COLUMN IF EXISTS expires_at");
            jdbcTemplate.execute("ALTER TABLE reservations DROP COLUMN IF EXISTS confirmed_at");
            log.info("Esquema de base de datos verificado.");
        } catch (Exception e) {
            log.debug("Migracion saltada: {}", e.getMessage());
        }

        try {
            if (zoneRepo.count() == 0) {
                zoneRepo.save(GeographicZone.builder().id("zone-1").active(true).build());
                zoneRepo.save(GeographicZone.builder().id("zone-2").active(true).build());
                zoneRepo.save(GeographicZone.builder().id("zone-3").active(true).build());
            }

            // Limpieza de datos demo
            reservationRepo.deleteAll();
            windowRepo.deleteAll();
            // zoneRepo.deleteAll();

            if (windowRepo.count() == 0) {
                Resource resource = resourceLoader.getResource("classpath:data/windows.json");
                try (InputStream is = resource.getInputStream()) {
                    List<WindowImportDTO> imports = objectMapper.readValue(is, new TypeReference<>() {
                    });
                    for (WindowImportDTO dto : imports) {
                        log.info("Cargando ventana: {}", dto.getId());

                        windowRepo.save(DeliveryWindow.builder()
                                .id(dto.getId())
                                .date(dto.getDate())
                                .startTime(dto.getStart())
                                .endTime(dto.getEnd())
                                .capacityTotal(dto.getCapacityTotal())
                                .capacityByZone(dto.getCapacityByZone())
                                .price(new BigDecimal("1990"))
                                .active(true)
                                .build());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to initialize data: {}", e.getMessage(), e);
        }
    }

    @Data
    private static class WindowImportDTO {
        private String id;
        private LocalDate date;
        private LocalTime start;
        private LocalTime end;
        private Integer capacityTotal;
        private Map<String, Integer> capacityByZone;
    }
}
