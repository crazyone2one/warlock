<script setup lang="ts">
import {type DropdownOption} from "naive-ui";
import {watchEffect} from "vue";

const emit = defineEmits(['rePassParameter'])
const props = defineProps<{ moreAction?: DropdownOption[], showEdit?: boolean }>()
const options: Array<DropdownOption> = [
  {label: '删除', key: 'delete',}
]
const handleSelect = (key: string) => {
  emit('rePassParameter', key)
}
watchEffect(() => {
  if (props.moreAction && props.moreAction.length > 0) {
    options.unshift(...props.moreAction)
  }
}, {})
</script>

<template>
  <n-flex>
    <n-button v-show="showEdit" text type="info" @click="emit('rePassParameter', 'edit')">edit</n-button>
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