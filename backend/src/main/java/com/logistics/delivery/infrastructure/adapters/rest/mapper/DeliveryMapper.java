package com.logistics.delivery.infrastructure.adapters.rest.mapper;

import com.logistics.delivery.domain.model.DeliveryWindow;
import com.logistics.delivery.domain.model.GeographicZone;
import com.logistics.delivery.domain.model.Reservation;
import com.logistics.delivery.infrastructure.adapters.rest.dto.ReservationResponse;
import com.logistics.delivery.infrastructure.adapters.rest.dto.WindowSlotDTO;
import com.logistics.delivery.infrastructure.adapters.rest.dto.ZoneResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    ZoneResponseDTO toZoneResponse(GeographicZone zone);

    List<ZoneResponseDTO> toZoneResponseList(List<GeographicZone> zones);

    @Mapping(target = "windowId", source = "windowId")
    @Mapping(target = "zoneId", source = "zoneId")
    ReservationResponse toReservationResponse(Reservation reservation);
}
