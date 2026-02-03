<script setup lang="ts">
import {type FormInst, type FormItemRule, NPopover} from "naive-ui";
import {computed, ref, useAttrs, watch} from "vue";
import {useI18n} from "vue-i18n";

interface FieldConfig {
  field?: string;
  rules?: FormItemRule[];
  placeholder?: string;
  maxLength?: number;
  isTextArea?: boolean;
  nameExistTipText?: string; // 添加重复提示文本
}

export interface ConfirmValue {
  field: string;
  id?: string;
}

const show = defineModel<boolean>('show', {type: Boolean, default: false});
const props = withDefaults(
    defineProps<{
      title: string; // 文本提示标题
      subTitleTip?: string; // 子内容提示
      isDelete?: boolean; // 当前使用是否是移除
      loading?: boolean;
      okText?: string; // 确定按钮文本
      cancelText?: string;
      popupContainer?: string;
      allNames?: string[]; // 添加或者重命名名称重复
      nodeId?: string; // 节点 id
      fieldConfig?: FieldConfig; // 表单配置项
    }>(),
    {
      isDelete: true, // 默认移除pop
      okText: 'common.remove',
    }
);
const emits = defineEmits<{
  (e: 'confirm', formValue: ConfirmValue, cancel?: () => void): void;
  (e: 'cancel'): void;
  (e: 'update:visible', visible: boolean): void;
}>();
const {t} = useI18n();
const attrs = useAttrs();
const titleClass = computed(() => {
  return props.isDelete
      ? 'ml-2 font-medium  text-[14px]'
      : 'mb-[8px] font-medium  text-[14px] leading-[22px]';
});
const reset = () => {
  form.value.field = '';
  formRef.value?.restoreValidation();
};
const handleCancel = () => {
  show.value = false;
  emits('cancel');
  reset()
};
const formRef = ref<FormInst | null>(null)
const form = ref({
  field: props.fieldConfig?.field || '',
});
const emitConfirm = () => emits('confirm', {...form.value, id: props.nodeId}, handleCancel);
const handleConfirm = () => {
  if (!formRef.value) {
    emitConfirm();
    return;
  }
  formRef.value?.validate((errors) => {
    if (!errors) {
      emitConfirm();
    }
  });
};
watch(
    () => props.fieldConfig?.field,
    (val) => {
      form.value.field = val || '';
    }
);
</script>

<template>
  <n-popover trigger="click" :show="show" :class="props.isDelete ? 'w-[352px]' : ''"
             v-bind="attrs" :position="props.isDelete ? 'right-end' : 'bottom'">
    <template #trigger>
      <slot></slot>
    </template>
    <div class="flex flex-row flex-nowrap items-center">
      <slot name="icon">
        <div v-if="props.isDelete"
             class="mr-[2px] text-[24px] text-red-6 i-solar:shield-warning-bold-duotone"
        />
      </slot>
      <div :class="[titleClass]">
        {{ props.title || '' }}
      </div>
      <!-- 描述展示 -->
      <div v-if="props.subTitleTip" class="ml-8 mt-2 text-sm">
        {{ props.subTitleTip }}
      </div>
      <n-form v-else ref="formRef">
        <n-form-item>

        </n-form-item>
      </n-form>
      <div class="mb-1 mt-4 flex flex-row flex-nowrap justify-end gap-2">
        <n-button secondary size="tiny" :disabled="props.loading" @click="handleCancel">
          {{ props.cancelText || t('common.cancel') }}
        </n-button>
        <n-button type="primary" size="tiny" :loading="props.loading" @click="handleConfirm">
          {{ t(props.okText) || t('common.confirm') }}
        </n-button>
      </div>
    </div>
  </n-popover>
</template>

<style scoped>
:deep(.n-popover__content) {
  padding: 16px;
}
</style>