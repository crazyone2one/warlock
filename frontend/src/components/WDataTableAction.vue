<script setup lang="ts">
const emit = defineEmits(['editProject', 'deleteProject'])
</script>

<template>
  <n-flex>
    <n-button text type="info" @click="emit('editProject')">edit</n-button>
    <n-button text type="error" @click="emit('deleteProject')">delete</n-button>
    <slot></slot>
  </n-flex>
</template>

<style scoped>

</style>