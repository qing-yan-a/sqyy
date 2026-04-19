<script setup lang="ts">
const model = defineModel<boolean>({ required: true })

defineProps<{
  title: string
  width?: string
}>()
</script>

<template>
  <el-dialog v-model="model" :title="title" :width="width ?? '720px'">
    <slot />
    <template #footer>
      <slot name="footer" />
    </template>
  </el-dialog>
</template>
