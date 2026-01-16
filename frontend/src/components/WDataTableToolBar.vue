<script setup lang="ts">
const emit = defineEmits(['add', 'refresh'])
</script>

<template>
  <n-flex justify="space-between">
    <div class="ml-1">
      <n-button text type="primary" @click="emit('add')">
        <template #icon>
          <n-icon>
            <div class="i-solar:add-circle-linear"/>
          </n-icon>
        </template>
      </n-button>
      <slot name="left"></slot>
    </div>
    <div>
      <n-button text type="info" @click="emit('refresh')">
        <template #icon>
          <n-icon>
            <div class="i-solar:refresh-circle-linear"/>
          </n-icon>
        </template>
      </n-button>
      <slot name="right"></slot>
    </div>
  </n-flex>
</template>

<style scoped>

</style>