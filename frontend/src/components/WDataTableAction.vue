<script setup lang="ts">
import {type DropdownOption} from "naive-ui";

const emit = defineEmits(['editProject', 'deleteProject'])
const options: Array<DropdownOption> = [
  {label: '删除', key: 'delete',}
]
const handleSelect = (key: string) => {
  if (key === 'delete') {
    emit('deleteProject')
  }
}
</script>

<template>
  <n-flex>
    <n-button text type="info" @click="emit('editProject')">edit</n-button>
    <slot></slot>
    <n-dropdown trigger="click" :options="options" @select="handleSelect">
      <n-button text>
        <n-icon :size="18">
          <div class="i-solar:alt-arrow-down-bold"/>
        </n-icon>
      </n-button>
    </n-dropdown>
  </n-flex>
</template>

<style scoped>

</style>