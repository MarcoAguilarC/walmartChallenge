<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useDeliveryStore } from "../store/delivery";
import { deliveryApi } from "../api/delivery";
import type { Zone } from "../types/delivery";
import { X, Search, MapPin, Check } from "lucide-vue-next";

defineProps<{
  isOpen: boolean;
}>();

const emit = defineEmits(["close"]);

const store = useDeliveryStore();
const zones = ref<Zone[]>([]);
const isLoading = ref(false);
const searchQuery = ref("");

onMounted(async () => {
  try {
    isLoading.value = true;
    zones.value = await deliveryApi.getZones();
  } catch (e) {
    console.error("Failed to load zones", e);
  } finally {
    isLoading.value = false;
  }
});

const filteredZones = computed(() => {
  if (!searchQuery.value) return zones.value;
  const query = searchQuery.value.toLowerCase();
  return zones.value.filter((zone) =>
    zone.id.toLowerCase().includes(query)
  );
});

const handleZoneSelect = (zone: Zone) => {
  store.setZone(zone.id);
  emit("close");
};
</script>

<template>
  <Transition name="fade">
    <div
      v-if="isOpen"
      class="fixed inset-0 z-50 flex items-center justify-center p-4"
    >
      <div
        class="absolute inset-0 bg-black/60 backdrop-blur-sm"
        @click="$emit('close')"
      ></div>

      <div
        class="relative bg-white w-full max-w-md rounded-2xl shadow-2xl overflow-hidden animate-in fade-in zoom-in duration-200"
      >
        <div
          class="p-4 bg-walmart-blue text-white flex items-center justify-between"
        >
          <h2 class="text-lg font-bold">Seleccionar Zona</h2>
          <button
            @click="$emit('close')"
            class="p-1 hover:bg-white/10 rounded-full transition-colors"
          >
            <X class="w-6 h-6" />
          </button>
        </div>

        <div class="p-4 border-b border-gray-100">
          <div class="relative">
            <Search
              class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400"
            />
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Buscar zona..."
              class="w-full pl-10 pr-4 py-2 bg-gray-50 border border-gray-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-walmart-blue/20"
            />
          </div>
        </div>

        <div class="max-h-96 overflow-y-auto p-4 space-y-2">
          <div
            v-if="isLoading"
            class="flex flex-col items-center py-8 text-gray-400"
          >
            <div
              class="w-8 h-8 border-4 border-walmart-blue border-t-transparent rounded-full animate-spin mb-4"
            ></div>
            <p>Cargando zonas...</p>
          </div>

          <div
            v-else-if="filteredZones.length === 0"
            class="text-center py-8 text-gray-400"
          >
            <p>No se encontraron zonas</p>
          </div>

          <button
            v-for="zone in filteredZones"
            :key="zone.id"
            @click="handleZoneSelect(zone)"
            class="w-full flex items-center gap-4 p-4 rounded-xl border-2 transition-all hover:bg-gray-50 text-left"
            :class="
              store.selectedZoneId === zone.id
                ? 'border-walmart-blue bg-blue-50'
                : 'border-gray-50'
            "
          >
            <div
              class="p-2 bg-gray-100 rounded-lg text-gray-500"
              :class="{
                'bg-blue-100 text-walmart-blue':
                  store.selectedZoneId === zone.id,
              }"
            >
              <MapPin class="w-5 h-5" />
            </div>
            <div class="flex-grow">
              <p class="font-bold text-gray-900">{{ zone.id.toUpperCase() }}</p>
            </div>
            <Check
              v-if="store.selectedZoneId === zone.id"
              class="w-6 h-6 text-walmart-blue"
            />
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
