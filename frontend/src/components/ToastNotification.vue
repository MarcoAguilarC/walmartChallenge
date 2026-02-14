<script setup lang="ts">
import { CheckCircle, XCircle, X } from 'lucide-vue-next';
import { watch } from 'vue';

const props = defineProps<{
  show: boolean;
  type: 'success' | 'error';
  message: string;
}>();

const emit = defineEmits(['close']);

// Auto-close after 5 seconds
watch(() => props.show, (show) => {
  if (show) {
    setTimeout(() => {
      emit('close');
    }, 5000);
  }
});
</script>

<template>
  <Transition name="toast">
    <div 
      v-if="show" 
      class="fixed top-24 right-4 z-50 max-w-sm w-full bg-white rounded-xl shadow-lg border-l-4 p-4 flex items-start gap-4"
      :class="type === 'success' ? 'border-green-500' : 'border-red-500'"
    >
      <div class="flex-shrink-0">
        <CheckCircle v-if="type === 'success'" class="w-6 h-6 text-green-500" />
        <XCircle v-else class="w-6 h-6 text-red-500" />
      </div>
      
      <div class="flex-grow pt-0.5">
        <h3 class="font-bold text-gray-900 leading-tight mb-1">
          {{ type === 'success' ? '¡Éxito!' : 'Error' }}
        </h3>
        <p class="text-sm text-gray-600 leading-relaxed">{{ message }}</p>
      </div>

      <button @click="$emit('close')" class="flex-shrink-0 text-gray-400 hover:text-gray-600 transition-colors">
        <X class="w-5 h-5" />
      </button>
    </div>
  </Transition>
</template>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.toast-enter-from,
.toast-leave-to {
  transform: translateX(100%) scale(0.9);
  opacity: 0;
}
</style>
