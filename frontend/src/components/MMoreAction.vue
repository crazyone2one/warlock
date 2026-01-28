<script setup lang="ts">
import type {ActionsItem} from "/@/api/types.ts";
import {computed, ref, watch} from "vue";
import {hasAnyPermission} from "/@/utils/permission.ts";

const props = defineProps<{
  list: ActionsItem[];
  trigger?: 'hover' | 'click' | 'focus' | 'manual'
}>();
const emit = defineEmits(['select', 'close', 'open']);
const show = ref(false);
const isHasAllPermission = computed(() => {
  const permissionList = props.list.map((item) => {
    return item.permission || [];
  });
  const permissionResult = permissionList.flat();
  return hasAnyPermission(permissionResult);
});
const handleSelect = (key: string) => {
  const item = props.list.find((e: ActionsItem) => e.eventTag === key);
  emit('select', item);
}
const visibleChange = (val: boolean) => {
  if (!val) {
    emit('close');
  }
}
watch(
    () => show.value,
    (val) => {
      if (val) {
        emit('open');
      }
    }
);
</script>

<template>
<span>
  <n-dropdown v-model:show="show" :trigger="props.trigger || 'hover'" :options="list" @select="handleSelect"
              @update-show="visibleChange">
      <n-button v-if="isHasAllPermission" text>
        <n-icon :size="18">
          <div class="i-solar:menu-dots-outline"/>
        </n-icon>
      </n-button>
    </n-dropdown>
</span>
</template>

<style scoped>

</style>