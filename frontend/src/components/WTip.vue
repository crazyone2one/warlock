<script setup lang="ts">
import {NTooltip} from "naive-ui";

const {content = 'sing a song', size = 24} = defineProps<{ content?: string, size?: number }>()
</script>

<template>
  <n-tooltip trigger="hover">
    <template #trigger>
      <n-button text class="ml-2">
        <template #icon>
          <n-icon :size="size">
            <div class="i-solar:question-circle-bold-duotone text-orange"/>
          </n-icon>
        </template>
      </n-button>

    </template>
    {{ content }}
  </n-tooltip>
</template>

<style scoped>

</style>