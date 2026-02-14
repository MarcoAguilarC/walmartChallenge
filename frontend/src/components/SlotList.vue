<script setup lang="ts">
import { computed } from "vue";
import { useDeliveryStore } from "../store/delivery";
import { useQuery } from "@tanstack/vue-query";
import { deliveryApi } from "../api/delivery";
import { Clock, AlertCircle } from "lucide-vue-next";

const store = useDeliveryStore();

const today = "2026-01-28";

const { data, isLoading, isError } = useQuery({
  queryKey: ["windows", computed(() => store.selectedZoneId), today],
  queryFn: () => deliveryApi.getWindows(store.selectedZoneId || "", today, 7),
  enabled: computed(() => !!store.selectedZoneId),
});

const currentWindows = computed(() => {
  const dateObj = data.value?.availableDates.find(
    (d: any) => d.date === store.selectedDate,
  );
  return (dateObj?.windows || []).filter((w: any) => w.available);
});

const selectSlot = (id: string) => {
  store.setSlot(id);
};

const formatPrice = (price: number) => {
  return new Intl.NumberFormat("es-CL", {
    style: "currency",
    currency: "CLP",
    minimumFractionDigits: 0,
  }).format(price);
};
</script>

<template>
  <div class="space-y-4">
    <div v-if="isLoading" class="space-y-4">
      <div
        v-for="i in 4"
        :key="i"
        class="h-16 bg-gray-50 animate-pulse rounded-xl border border-gray-100"
      ></div>
    </div>

    <div
      v-else-if="isError"
      class="bg-red-50 p-6 rounded-2xl border border-red-100 text-center"
    >
      <AlertCircle class="w-12 h-12 text-red-400 mx-auto mb-3" />
      <p class="text-red-700 font-bold">Error al cargar horarios</p>
      <p class="text-red-500 text-sm mt-1">
        Por favor, intenta nuevamente más tarde.
      </p>
    </div>

    <div
      v-else-if="currentWindows.length === 0"
      class="py-12 text-center text-gray-400"
    >
      <Clock class="w-16 h-16 mx-auto mb-4 opacity-20" />
      <p class="text-lg font-medium">No hay horarios disponibles</p>
      <p class="text-sm">Para esta fecha y zona seleccionada.</p>
    </div>

    <template v-else>
      <button
        v-for="window in currentWindows"
        :key="window.id"
        @click="selectSlot(window.id)"
        :disabled="!window.available"
        class="w-full flex items-center justify-between p-4 bg-white border-2 rounded-xl transition-all relative overflow-hidden group"
        :class="[
          store.selectedSlotId === window.id
            ? 'border-walmart-blue bg-blue-50 shadow-md ring-2 ring-walmart-blue ring-opacity-10'
            : 'border-gray-50 hover:border-gray-200',
          !window.available
            ? 'opacity-50 grayscale cursor-not-allowed bg-gray-50'
            : 'cursor-pointer active:scale-[0.99]',
        ]"
      >
        <div class="flex items-center gap-4">
          <div
            class="w-6 h-6 rounded-full border-2 flex items-center justify-center transition-colors shadow-inner"
            :class="
              store.selectedSlotId === window.id
                ? 'border-walmart-blue bg-walmart-blue shadow-blue-200'
                : 'border-gray-300 bg-white'
            "
          >
            <div
              v-if="store.selectedSlotId === window.id"
              class="w-2 h-2 rounded-full bg-white shadow-sm"
            ></div>
          </div>
          <span
            class="font-bold text-gray-700 group-hover:text-gray-900 transition-colors"
            >{{ window.display }}</span
          >

          <span
            v-if="window.remainingCapacity < 5 && window.available"
            class="text-[10px] bg-walmart-yellow/20 text-walmart-gray px-2 py-0.5 rounded-full font-bold"
          >
            ¡Sólo {{ window.remainingCapacity }} cupos!
          </span>
        </div>

        <div class="text-right">
          <p class="font-black text-gray-900 leading-none">
            {{ formatPrice(window.price) }}
          </p>
          <p
            v-if="!window.available"
            class="text-[10px] text-red-500 font-bold uppercase mt-1"
          >
            Agotado
          </p>
        </div>

        <!-- Selection indicator line -->
        <div
          v-if="store.selectedSlotId === window.id"
          class="absolute left-0 top-0 bottom-0 w-1.5 bg-walmart-blue"
        ></div>
      </button>
    </template>
  </div>
</template>
