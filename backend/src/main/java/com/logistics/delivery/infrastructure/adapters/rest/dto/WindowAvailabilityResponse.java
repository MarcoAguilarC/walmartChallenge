package com.logistics.delivery.infrastructure.adapters.rest.dto;

import java.util.List;

public class WindowAvailabilityResponse {
    private List<AvailableDateDTO> availableDates;

    public WindowAvailabilityResponse() {
    }

    public List<AvailableDateDTO> getAvailableDates() {
        return availableDates;
    }

    public void setAvailableDates(List<AvailableDateDTO> availableDates) {
        this.availableDates = availableDates;
    }

    public static WindowAvailabilityResponseBuilder builder() {
        return new WindowAvailabilityResponseBuilder();
    }

    public static class WindowAvailabilityResponseBuilder {
        private List<AvailableDateDTO> availableDates;

        public WindowAvailabilityResponseBuilder availableDates(List<AvailableDateDTO> availableDates) {
            this.availableDates = availableDates;
            return this;
        }

        public WindowAvailabilityResponse build() {
            WindowAvailabilityResponse r = new WindowAvailabilityResponse();
            r.availableDates = availableDates;
            return r;
        }
    }
}
