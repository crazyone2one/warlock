<script setup lang="ts">
import {useI18n} from "vue-i18n";
import WModal from "/@/components/WModal.vue";
import {reactive, ref, watch} from "vue";
import {type FormInst} from "naive-ui";
import {useRequest} from "alova/client";
import {userGroupApi} from "/@/api/methods/userGroup.ts";

const {t} = useI18n()
const {currentId = ''} = defineProps<{ currentId: string }>();
const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false});
const emit = defineEmits<{ (e: 'cancel', shouldSearch: boolean): void; }>();

const formRef = ref<FormInst | null>(null)
const {
  send: fetchUserOptions,
  data: userOptions
} = useRequest((id, keyword) => userGroupApi.getSystemUserGroupOption(id, keyword), {immediate: false})

const model = reactive({
  name: [],
});
const handleCancel = (shouldSearch = false) => {
  model.name = [];
  emit('cancel', shouldSearch);
};

const {
  send: addUserToUserGroup,
  loading
} = useRequest(formDate => userGroupApi.addUserToUserGroup(formDate), {immediate: false})
const handleSubmit = () => {
  formRef.value?.validate(error => {
    if (!error) {
      addUserToUserGroup({roleId: currentId, userIds: model.name}).then(() => {
        handleCancel(true);
        window.$message.success(t('common.addSuccess'))
      })
    }
  })
}
watch(() => showModal.value, (newValue) => {
  if (newValue) {
    fetchUserOptions(currentId, '');
  }
})
</script>

<template>
  <w-modal v-model:show-modal="showModal" :title="t('system.userGroup.addUser')"
           :ok-text="'system.userGroup.add'"
           :disabled="model.name.length===0"
           :loading="loading"
           @cancel="handleCancel" @submit="handleSubmit">
    <n-form
        ref="formRef"
        class="rounded-[4px]"
        :model="model"
        label-placement="top"
    >
      <n-form-item path="name" :label="t('system.userGroup.user')"
                   :rule="{required: true, message: t('system.userGroup.pleaseSelectUser') }">
        <n-select v-model:value="model.name" multiple filterable :options="userOptions"
                  label-field="name" value-field="id"/>
      </n-form-item>
    </n-form>
  </w-modal>
</template>

<style scoped>

</style>