<script setup lang="ts">
import {type FormInst, type FormItemRule, NPopover} from "naive-ui";
import {ref, watchEffect} from "vue";
import type {AuthScopeEnumType} from "/@/utils/common-enum.ts";
import type {UserGroupItem} from "/@/api/types/user-group.ts";
import {useI18n} from "vue-i18n";
import {useForm} from "alova/client";
import {userGroupApi} from "/@/api/methods/userGroup.ts";

const {t} = useI18n();
// const systemType = inject<AuthScopeEnumType>('systemType');
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
const formRef = ref<FormInst | null>(null)
const rules = {
  name: [{
    validate(_rule: FormItemRule, value: string) {
      if (!value) {
        return new Error(t('system.userGroup.userGroupNameIsNotNone'));
      } else {
        if (value === props.defaultName) {
          return true
        } else {
          const isExist = props.list.some((item) => item.name === value);
          if (isExist) {
            return new Error(t('system.userGroup.userGroupNameIsExist', {name: value}));
          }
        }
        if (value.length > 255) {
          return new Error(t('common.nameIsTooLang'));
        }
        return true
      }
    }
  }]
}
const {form, loading, send} = useForm(data => userGroupApi.updateOrAddUserGroup(data), {
  initialForm: {
    name: '',
    id: props.id,
    type: props.authScope,
    code: ''
  },
  resetAfterSubmiting: true
})
const handleCancel = () => {
  form.value.name = '';
  emit('cancel', false);
};
const handleSubmit = () => {
  formRef.value?.validate((error) => {
    if (!error) {
      send().then(res => {
        window.$message.success(props.id ? t('system.userGroup.updateUserGroupSuccess') : t('system.userGroup.addUserGroupSuccess'))
        emit('submit', res.id);
        handleCancel();
      })
    }
  });
};
const handleOutsideClick = () => {
  if (currentVisible.value) {
    handleCancel();
  }
};
watchEffect(() => {
  currentVisible.value = props.visible;
  form.value.name = props.defaultName || '';
});
</script>

<template>
  <n-popover :show="currentVisible" trigger="hover">
    <template #trigger>
      <slot></slot>
    </template>
    <div v-outer="handleOutsideClick">
      <div>
        <n-form ref="formRef" :rules="rules" size="small" label-placement="left">
          <div class="mb-[8px] text-[14px] font-medium">
            {{ props.id ? t('system.userGroup.rename') : t('system.userGroup.createUserGroup') }}
          </div>
          <span v-if="!props.id" class="mt-[8px] text-[13px] font-medium">
                {{ t('system.userGroup.createUserGroupTip') }}
              </span>
          <n-form-item path="name" label="用户组名称">
            <n-input v-model:value="form.name" :maxlength="255" :placeholder="t('system.userGroup.searchHolder')"
                     clearable/>
          </n-form-item>
          <n-form-item path="code" label="用户组code">
            <n-input v-model:value="form.code" :maxlength="255" :placeholder="t('system.userGroup.code')"
                     clearable/>
          </n-form-item>
        </n-form>
      </div>
      <n-flex>
        <n-button secondary size="tiny" :disabled="loading" @click="handleCancel">{{ t('common.cancel') }}</n-button>
        <n-button type="primary" size="tiny" :loading="loading" :disabled="form.name.length === 0"
                  @click="handleSubmit">
          {{ props.id ? t('common.confirm') : t('common.create') }}
        </n-button>
      </n-flex>
    </div>
  </n-popover>
</template>

<style scoped>

</style>