<script setup lang="ts">
import { computed, watch, ref } from "vue";
import { useDeliveryStore } from "../store/delivery";
import { useQuery } from "@tanstack/vue-query";
import { deliveryApi } from "../api/delivery";
import { format, parseISO } from "date-fns";
import { Calendar } from "lucide-vue-next";

const store = useDeliveryStore();

// Start date for the slider (defaults to seeded data date)
const startDate = ref("2026-01-28");

const { data, isLoading } = useQuery({
  queryKey: ["windows", computed(() => store.selectedZoneId), startDate],
  queryFn: () =>
    deliveryApi.getWindows(store.selectedZoneId || "", startDate.value, 7),
  enabled: computed(() => !!store.selectedZoneId),
});

const availableDates = computed(() => data.value?.availableDates || []);

// Auto-select first date if current selection is empty or out of range
watch(availableDates, (newDates) => {
  if (Array.isArray(newDates) && newDates.length > 0) {
    // If currently selected date is not in the new range, select the first available
    const isSelectedInNewRange = newDates.some(
      (d) => d.date === store.selectedDate,
    );
    if (!store.selectedDate || !isSelectedInNewRange) {
      const first = newDates[0];
      if (first && first.date) {
        store.setDate(first.date);
      }
    }
  }
});

const handleDateSelect = (date: string) => {
  store.setDate(date);
};

const isToday = (dateStr: string) => {
  return dateStr === format(new Date(), "yyyy-MM-dd");
};

const dateInput = ref<HTMLInputElement | null>(null);

const updateStartDate = (event: Event) => {
  const input = event.target as HTMLInputElement;
  if (input.value) {
    startDate.value = input.value;
  }
};

const openDatePicker = (event: Event) => {
  // Prevent default label behavior to avoid double-triggering or focus issues
  event.preventDefault();
  dateInput.value?.showPicker();
};
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between px-1">
      <label
        class="relative flex items-center gap-2 text-sm font-bold text-gray-700 bg-white border border-gray-200 rounded-lg px-3 py-2 shadow-sm cursor-pointer hover:bg-gray-50 transition-colors w-full sm:w-auto"
        @click="openDatePicker"
      >
        <Calendar class="w-4 h-4 text-walmart-blue" />
        <span>Ver desde:</span>
        <!-- Use parseISO to ensure local date interpretation -->
        <span class="font-normal">{{
          format(parseISO(startDate), "dd-MM-yyyy")
        }}</span>

        <!-- Invisible input for value binding, pointer-events-none so label click handles trigger -->
        <input
          ref="dateInput"
          type="date"
          :value="startDate"
          @input="updateStartDate"
          class="absolute inset-0 w-full h-full opacity-0 pointer-events-none"
          :min="format(new Date(), 'yyyy-MM-dd')"
          @click.stop
        />
      </label>
    </div>

    <div class="relative overflow-hidden">
      <div v-if="isLoading" class="flex gap-4 overflow-x-auto pb-8 px-1">
        <div
          v-for="i in 7"
          :key="i"
          class="flex-shrink-0 w-16 h-20 bg-gray-50 animate-pulse rounded-full border border-gray-100"
        ></div>
      </div>

      <div v-else class="flex gap-4 overflow-x-auto pb-8 px-1 scrollbar-hide">
        <button
          v-for="item in availableDates"
          :key="item.date"
          @click="handleDateSelect(item.date)"
          :disabled="!item.windows.some((w) => w.available)"
          class="flex-shrink-0 w-16 h-20 flex flex-col items-center justify-center rounded-full border-2 transition-all relative"
          :class="[
            store.selectedDate === item.date
              ? 'border-walmart-blue bg-walmart-blue text-white shadow-md scale-110 z-10'
              : 'border-gray-100 bg-white text-gray-500 hover:border-gray-300',
            !item.windows.some((w) => w.available)
              ? 'opacity-40 grayscale cursor-not-allowed bg-gray-50'
              : 'cursor-pointer',
          ]"
        >
          <span class="text-[10px] font-bold uppercase">{{
            isToday(item.date) ? "Hoy" : item.dayName
          }}</span>
          <span class="text-lg font-black leading-tight"
            >{{ item.dayNumber.split("/")[0] }}/{{
              item.dayNumber.split("/")[1]
            }}</span
          >

          <div
            v-if="!item.windows.some((w) => w.available)"
            class="absolute -bottom-6 text-[10px] text-gray-400 font-bold whitespace-nowrap"
          >
            Agotado
          </div>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
