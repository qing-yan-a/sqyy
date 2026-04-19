<script setup lang="ts">
defineProps<{
  label: string
  value: string | number
  helper?: string
}>()
</script>

<template>
  <div class="stat-card">
    <div class="stat-card__label">{{ label }}</div>
    <div class="stat-card__value">{{ value }}</div>
    <div v-if="helper" class="stat-card__helper">{{ helper }}</div>
  </div>
</template>
