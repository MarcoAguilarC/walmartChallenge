export interface Zone {
    id: string;
}

export interface DeliveryWindow {
    id: string;
    startTime: string;
    endTime: string;
    display: string;
    price: number;
    available: boolean;
    remainingCapacity: number;
}

export interface AvailableDate {
    date: string;
    dayName: string;
    dayNumber: string;
    windows: DeliveryWindow[];
}

export interface WindowAvailabilityResponse {
    availableDates: AvailableDate[];
}

export interface ReservationRequest {
    windowId: string;
    zoneId: string;
    userId: string;
}

export interface ReservationResponse {
    id: string;
    windowId: string;
    zoneId: string;
    price: number;
    status: 'PENDING' | 'CONFIRMED' | 'EXPIRED' | 'CANCELLED';
}

export type ServiceType = 'PICKUP' | 'DELIVERY';
