<script setup lang="ts">
import WModal from "/@/components/WModal.vue";
import {ref} from "vue";
import {type FormInst, NFormItemGi, NGrid} from "naive-ui";
import {useForm} from "alova/client";
import {scheduleApi} from "/@/api/methods/schedule.ts";
import {useAppStore} from "/@/store";
import WCronSelect from "/@/components/WCronSelect.vue";
import WTip from "/@/components/WTip.vue";

const appStore = useAppStore()
const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false});
const formRef = ref<FormInst | null>(null)
const title = ref('添加任务')
const emit = defineEmits<{
  (e: 'cancel', shouldSearch: boolean): void;
}>();
const {form: model, reset, send} = useForm(formData => scheduleApi.createOrUpdateSchedule(formData), {
  initialForm: {
    id: '',
    name: '',
    enable: true,
    value: '',
    projectId: appStore.currentProjectId,
    job: '',
    jobKey: crypto.randomUUID().replace(/-/g, ''),
    type: 'cron'
  },
  resetAfterSubmiting: true,
})
const rules = {
  name: [{required: true, message: '请输入任务名称', trigger: 'blur'}],
  value: [{required: true, message: '请选择或者录入对应的cron'}],
}
const handleCancel = (shouldSearch: boolean) => {
  reset()
  showModal.value = false
  emit('cancel', shouldSearch)
}
const handleSubmit = () => {
  formRef.value?.validate((error) => {
    if (!error) {
      console.log(model.value)
      window.$message.success('保存成功')
    }
  })
}
</script>

<template>
  <w-modal v-model:show-modal="showModal" :title="title"
           @cancel="handleCancel(false)"
           @submit="handleSubmit">
    <template #content>
      <n-form
          ref="formRef"
          :model="model"
          :rules="rules"
          label-placement="top"
          :label-width="null"
          require-mark-placement="right-hanging"
      >
        <n-grid :cols="12" :x-gap="12">
          <n-form-item-gi :span="6" label="任务名称" path="name">
            <n-input v-model:value="model.name" placeholder="请输入任务名称"/>
          </n-form-item-gi>
          <n-form-item-gi :span="6" label="任务cron" path="value">
            <w-cron-select v-model:model-value="model.value"/>
          </n-form-item-gi>
          <n-form-item-gi :span="12" label="任务描述" path="job">
            <n-input v-model:value="model.job" placeholder="请选择项目描述"/>
          </n-form-item-gi>
          <n-form-item-gi :span="6" label="任务状态">
            <n-switch v-model:value="model.enable"/>
            <w-tip content="未启用时不会创建定时任务"/>
          </n-form-item-gi>
        </n-grid>
      </n-form>
    </template>
  </w-modal>
</template>

<style scoped>

</style>