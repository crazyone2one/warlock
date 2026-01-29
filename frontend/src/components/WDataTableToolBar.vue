<script setup lang="ts">
const {addPermission = [], addIcon = 'i-solar:add-circle-linear'} = defineProps<{
  addPermission?: string[],
  addIcon?: string
}>()
const emit = defineEmits(['add', 'refresh'])
</script>

<template>
  <n-flex justify="space-between" class="mb-1">
    <div class="ml-5">
      <n-flex justify="space-between">
        <n-button v-permission.all="addPermission" text type="primary" @click="emit('add')">
          <template #icon>
            <n-icon>
              <div :class="addIcon"/>
            </n-icon>
          </template>
        </n-button>
        <slot name="left"></slot>
      </n-flex>
    </div>
    <n-flex>
      <div>
        <slot name="right"></slot>
      </div>
      <n-button text type="info" @click="emit('refresh')">
        <template #icon>
          <n-icon>
            <div class="i-solar:refresh-circle-linear"/>
          </n-icon>
        </template>
      </n-button>
      <slot name="right-end"></slot>
    </n-flex>
  </n-flex>
</template>

<style scoped>

</style>