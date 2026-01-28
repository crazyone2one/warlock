<script setup lang="ts">
import {NPopover} from "naive-ui";
import {inject, reactive, ref, watchEffect} from "vue";
import type {AuthScopeEnumType} from "/@/utils/common-enum.ts";
import type {UserGroupItem} from "/@/api/types/user-group.ts";
import {useI18n} from "vue-i18n";

const {t} = useI18n();
const systemType = inject<AuthScopeEnumType>('systemType');
const props = defineProps<{
  id?: string;
  list: UserGroupItem[];
  visible: boolean;
  defaultName?: string;
  // 权限范围
  authScope: AuthScopeEnumType;
}>();
const emit = defineEmits<{
  (e: 'cancel', value: boolean): void;
  (e: 'submit', currentId: string): void;
}>();
const currentVisible = ref(props.visible);
const form = reactive({
  name: '',
});
const handleCancel = () => {
  form.name = '';
  emit('cancel', false);
};
const handleOutsideClick = () => {
  if (currentVisible.value) {
    handleCancel();
  }
};
watchEffect(() => {
  currentVisible.value = props.visible;
  form.name = props.defaultName || '';
});
</script>

<template>
  <n-popover :show="currentVisible" trigger="hover">
    <template #trigger>
      <slot></slot>
    </template>
    <div v-outer="handleOutsideClick">
      <div>
        <n-form ref="formRef">
          <div class="mb-[8px] text-[14px] font-medium text-[var(--color-text-1)]">
            {{ props.id ? t('system.userGroup.rename') : t('system.userGroup.createUserGroup') }}
          </div>
          <n-form-item>
            <n-input :maxlength="255" :placeholder="t('system.userGroup.searchHolder')" clearable/>
            <span v-if="!props.id" class="mt-[8px] text-[13px] font-medium">
                {{ t('system.userGroup.createUserGroupTip') }}
              </span>
          </n-form-item>
        </n-form>
      </div>
      <n-flex>
        <n-button secondary size="tiny">{{ t('common.cancel') }}</n-button>
        <n-button type="primary" size="tiny"> {{ props.id ? t('common.confirm') : t('common.create') }}</n-button>
      </n-flex>
    </div>
  </n-popover>
</template>

<style scoped>

</style>