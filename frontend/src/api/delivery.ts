import axios from 'axios';
import type { WindowAvailabilityResponse, ReservationRequest, ReservationResponse, Zone } from '@/types/delivery';

const client = axios.create({
    baseURL: '/api/delivery',
    headers: {
        'Content-Type': 'application/json',
    },
});

export const deliveryApi = {
    getZones: () => client.get<Zone[]>('/zones').then(res => res.data),

    getWindows: (zoneId: string, startDate: string, days = 7) =>
        client.get<WindowAvailabilityResponse>('/windows', {
            params: { zoneId, startDate, days }
        }).then(res => res.data),

    createReservation: (request: ReservationRequest) =>
        client.post<ReservationResponse>('/reservations', request).then(res => res.data),

    confirmReservation: (id: string) =>
        client.post<ReservationResponse>(`/reservations/${id}/confirm`).then(res => res.data),

    cancelReservation: (id: string) =>
        client.delete(`/reservations/${id}`).then(res => res.data),
};
