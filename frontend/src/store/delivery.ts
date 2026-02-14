import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { ServiceType, ReservationResponse } from '@/types/delivery';


export const useDeliveryStore = defineStore('delivery', () => {
    const serviceType = ref<ServiceType>('DELIVERY');
    const selectedZoneId = ref<string>('zone-1');
    const selectedDate = ref<string>('2026-01-28');
    const selectedSlotId = ref<string | null>(null);
    const currentReservation = ref<ReservationResponse | null>(null);
    const isPending = ref(false);

    const isZoneModalOpen = ref(false);

    const setServiceType = (type: ServiceType) => {
        serviceType.value = type;
    };

    const setZone = (zoneId: string) => {
        selectedZoneId.value = zoneId;
        isZoneModalOpen.value = false;
        selectedSlotId.value = null;
    };

    const setDate = (date: string) => {
        selectedDate.value = date;
        selectedSlotId.value = null;
    };

    const setSlot = (id: string) => {
        selectedSlotId.value = id;
    };

    return {
        serviceType,
        selectedZoneId,
        selectedDate,
        selectedSlotId,
        currentReservation,
        isPending,
        isZoneModalOpen,
        setServiceType,
        setZone,
        setDate,
        setSlot
    };
});
