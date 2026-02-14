<script setup lang="ts">
import { useDeliveryStore } from '../store/delivery';
import { MapPin } from 'lucide-vue-next';

const store = useDeliveryStore();
</script>

<template>
  <div class="flex items-center justify-between p-4 bg-white border border-gray-100 rounded-xl shadow-sm">
    <div class="flex items-center gap-4">
      <div class="p-2 bg-walmart-light rounded-lg text-walmart-blue">
        <MapPin class="w-5 h-5" />
      </div>
      <div>
        <p class="text-sm font-bold text-gray-900">Pedro Perez</p>
        <p class="text-xs text-gray-500 uppercase tracking-wide font-medium">Zona Seleccionada: {{ store.selectedZoneId }}</p>
      </div>
    </div>
    <button 
      @click="store.isZoneModalOpen = true"
      class="text-sm font-bold text-walmart-blue hover:underline underline-offset-4"
    >
      Cambiar
    </button>
  </div>
</template>
