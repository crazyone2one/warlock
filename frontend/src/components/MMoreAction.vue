<script setup lang="ts">
import {computed, ref, watch} from "vue";
import {hasAnyPermission} from "/@/utils/permission.ts";
import {type DropdownOption} from "naive-ui";

const props = defineProps<{
  list: DropdownOption[];
  trigger?: 'hover' | 'click' | 'focus' | 'manual'
}>();
const emit = defineEmits(['select', 'close', 'open']);
const show = ref(false);
const isHasAllPermission = computed(() => {
  const permissionList = props.list.map((item) => {
    return item.permission || [];
  });
  const permissionResult = permissionList.flat();
  return hasAnyPermission(permissionResult as string[]);
});
const handleSelect = (key: string) => {
  const item = props.list.find((e: DropdownOption) => e.key === key);
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