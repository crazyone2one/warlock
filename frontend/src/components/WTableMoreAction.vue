<script setup lang="ts">
import type {DropdownOption} from "naive-ui";
import {computed, ref, watch} from "vue";
import {hasAnyPermission} from "/@/utils/permission.ts";

const {options = [], trigger = 'hover'} = defineProps<{
  options: DropdownOption[],
  trigger?: 'hover' | 'click' | 'focus' | 'manual'
}>()
const emit = defineEmits(['select', 'close', 'open']);
const visible = ref(false);
const visibleChange = (v: boolean) => {
  if (!v) {
    emit('close');
  }
}
const handleSelect = (_key: string | number, option: DropdownOption) => {
  emit('select', option);
}
const isHasAllPermission = computed(() => {
  const permissionList = options.map((item) => {
    return item.permission || [];
  });
  const permissionResult = permissionList.flat();
  return hasAnyPermission(permissionResult as string[]);
});
watch(
    () => visible.value,
    (val) => {
      if (val) {
        emit('open');
      }
    }
);
</script>

<template>
<span>
  <n-dropdown :show="visible" :trigger="trigger" :options="options" @select="handleSelect"
              @update-show="visibleChange">
      <n-button v-if="isHasAllPermission" text @click="visible = !visible" >
        <n-icon :size="24">
          <div class="i-solar:alt-arrow-down-bold"/>
        </n-icon>
      </n-button>
    </n-dropdown>
</span>
</template>

<style scoped>

</style>