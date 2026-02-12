<script setup lang="ts">
import {NTag} from "naive-ui";
import {type RequestDefinitionStatus, RequestDefinitionStatusEnum} from "/@/utils/api-enum.ts";
import {useI18n} from "vue-i18n";
import {computed} from "vue";

const props = defineProps<{
  status: RequestDefinitionStatus;
  size?: 'tiny' | 'small' | 'medium' | 'large';
}>();
const { t } = useI18n();
const statusMap = {
  [RequestDefinitionStatusEnum.DEPRECATED]: {
    color: 'text-gray-800',
    textColor: 'text-gray-400',
    text: 'apiTestManagement.deprecate',
  },
  [RequestDefinitionStatusEnum.PROCESSING]: {
    color: 'text-blue-200',
    textColor: 'text-blue-500',
    text: 'apiTestManagement.processing',
  },
  // [RequestDefinitionStatusEnum.UNDERWAY]: {
  //   bgColor: 'rgb(var(--link-2))',
  //   color: 'rgb(var(--link-5))',
  //   text: 'apiTestManagement.processing',
  // },
  [RequestDefinitionStatusEnum.DEBUGGING]: {
    color: 'text-blue-200',
    textColor: 'text-blue-600',
    text: 'apiTestManagement.debugging',
  },
  [RequestDefinitionStatusEnum.DONE]: {
    color: 'text-green-200',
    textColor: 'text-green-600',
    text: 'apiTestManagement.done',
  },
  // [RequestDefinitionStatusEnum.COMPLETED]: {
  //   bgColor: 'rgb(var(--success-2))',
  //   color: 'rgb(var(--success-6))',
  //   text: 'apiTestManagement.done',
  // },
};
const status = computed(() => {
  const config = statusMap[props.status];
  return {
    style: {
      textColor: config?.textColor,
      color: config?.color,
    },
    text: t(config?.text),
  };
});
</script>

<template>
  <n-tag :color="status.style" :size="props.size">
    {{ status.text }}
  </n-tag>
</template>

<style scoped>

</style>