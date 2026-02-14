<script setup lang="ts">
import { useDeliveryStore } from './store/delivery';
import { deliveryApi } from './api/delivery';
import { useQueryClient } from '@tanstack/vue-query';
import ZoneBanner from './components/ZoneBanner.vue';
import ServiceSelector from './components/ServiceSelector.vue';
import DateSlider from './components/DateSlider.vue';
import SlotList from './components/SlotList.vue';
import ZoneModal from './components/ZoneModal.vue';
import { X, ChevronLeft, Loader2 } from 'lucide-vue-next';

import ToastNotification from './components/ToastNotification.vue';
import { ref } from 'vue';

const store = useDeliveryStore();
const queryClient = useQueryClient();

const toast = ref({
  show: false,
  type: 'success' as 'success' | 'error',
  message: ''
});

const showToast = (type: 'success' | 'error', message: string) => {
  toast.value = { show: true, type, message };
};

const handleReserve = async () => {
    if (!store.selectedSlotId) return;
    
    try {
        store.isPending = true;
        const res = await deliveryApi.createReservation({
            windowId: store.selectedSlotId,
            zoneId: store.selectedZoneId,
            userId: 'session-aff8'
        });
        store.currentReservation = res;
        
        queryClient.invalidateQueries({ queryKey: ['windows'] });
        
        showToast('success', 'Reserva creada con éxito.');
    } catch (e: any) {
        console.error('Failed to create reservation', e);
        const msg = e.response?.data?.detail || 'Error al crear la reserva. Por favor intenta de nuevo.';
        showToast('error', msg);
    } finally {
        store.isPending = false;
    }
};
</script>

<template>
  <div class="min-h-screen bg-gray-50 flex flex-col items-center antialiased">
    <!-- Header -->
    <header class="w-full max-w-2xl bg-walmart-blue text-white p-6 flex items-center justify-between sticky top-0 z-20 shadow-md shrink-0">
      <div class="flex items-center gap-4">
        <button class="p-2 hover:bg-white/10 rounded-full transition-colors">
          <ChevronLeft class="w-6 h-6" />
        </button>
        <h1 class="text-xl font-bold tracking-tight">Reservar horario</h1>
      </div>
      <button class="p-2 hover:bg-white/10 rounded-full transition-colors">
        <X class="w-6 h-6" />
      </button>
    </header>

    <!-- Main Content -->
    <main class="w-full max-w-2xl bg-white shadow-xl min-h-[calc(100vh-80px)] p-6 pb-32 flex flex-col gap-8 relative z-10">
      
      <ServiceSelector />
      
      <ZoneBanner />

      <section>
        <h2 class="text-lg font-bold text-gray-900 mb-4 px-1">Elige tu Despacho a domicilio fecha y hora</h2>
        <DateSlider />
      </section>

      <div class="flex-grow">
        <SlotList />
      </div>

      <!-- Footer Action -->
      <footer class="fixed bottom-0 left-0 right-0 z-30 p-4 bg-white/80 backdrop-blur-md border-t border-gray-100 flex justify-center">
        <div class="w-full max-w-2xl px-2">
          <button 
              @click="handleReserve"
              :disabled="!store.selectedSlotId || store.isPending"
              class="walmart-btn-primary w-full shadow-lg hover:shadow-xl transform transition-all active:scale-[0.98] flex items-center justify-center gap-3 py-4 text-lg"
          >
              <Loader2 v-if="store.isPending" class="w-6 h-6 animate-spin" />
              {{ store.isPending ? 'Procesando...' : 'Reservar' }}
          </button>
        </div>
      </footer>
    </main>

    <!-- Modals -->
    <!-- Modals -->
    <ZoneModal :is-open="store.isZoneModalOpen" @close="store.isZoneModalOpen = false" />
    <ToastNotification 
      :show="toast.show" 
      :type="toast.type" 
      :message="toast.message" 
      @close="toast.show = false" 
    />
  </div>
</template>
