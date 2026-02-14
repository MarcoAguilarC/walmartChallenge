<script setup lang="ts">
import { useDeliveryStore } from '../store/delivery';
import { Truck, Car } from 'lucide-vue-next';

const store = useDeliveryStore();
</script>

<template>
  <div class="grid grid-cols-2 gap-4">
    <!-- Pickup Button -->
    <button 
      @click="store.setServiceType('PICKUP')"
      class="flex flex-col items-center justify-center p-6 border-2 rounded-2xl transition-all"
      :class="store.serviceType === 'PICKUP' ? 'border-walmart-blue bg-blue-50 text-walmart-blue shadow-md' : 'border-gray-100 text-gray-500 hover:border-gray-200'"
    >
      <div class="bg-gray-100 p-3 rounded-full mb-3" :class="{ 'bg-blue-100': store.serviceType === 'PICKUP' }">
        <Car class="w-8 h-8" />
      </div>
      <span class="font-bold">Retiro Pickup</span>
    </button>

    <!-- Delivery Button -->
    <button 
      @click="store.setServiceType('DELIVERY')"
      class="flex flex-col items-center justify-center p-6 border-2 rounded-2xl transition-all"
      :class="store.serviceType === 'DELIVERY' ? 'border-walmart-blue bg-blue-50 text-walmart-blue shadow-md' : 'border-gray-100 text-gray-400 hover:border-gray-200'"
    >
      <div class="bg-gray-100 p-3 rounded-full mb-3" :class="{ 'bg-blue-100': store.serviceType === 'DELIVERY' }">
        <Truck class="w-8 h-8" />
      </div>
      <span class="font-bold">Despacho a domicilio</span>
    </button>
  </div>
</template>
