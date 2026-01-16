<script setup lang="ts">
import WModal from "/@/components/WModal.vue";
import {computed, ref, watch} from "vue";
import type {IProjectItem} from "/@/api/types.ts";
import type {FormInst, FormRules} from "naive-ui";
import {useForm} from "alova/client";
import {projectApi} from "/@/api/methods/project.ts";

const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false});
const {
  currentProject = {description: "", enable: true, name: "", id: '', num: ''}
} = defineProps<{ currentProject?: IProjectItem | null; }>();
const formRef = ref<FormInst | null>(null)
const emit = defineEmits<{
  (e: 'cancel', shouldSearch: boolean): void;
}>();
const {form: model, reset, send} = useForm(formData => projectApi.createOrUpdateProject(formData), {
  initialForm: {
    id: '',
    name: '',
    description: '',
    enable: true,
    num: ''
  },
  resetAfterSubmiting: true,
})
const rules: FormRules = {
  name: {required: true, trigger: ['blur', 'input'], message: '请输入项目名称'},
  num: {required: true, trigger: ['blur', 'input'], message: '请输入项目编号'},
}
const handleCancel = (shouldSearch: boolean) => {
  reset()
  showModal.value = false
  emit('cancel', shouldSearch)
}
const handleSubmit = () => {
  formRef.value?.validate((error) => {
    if (!error) {
      send().then(() => {
        handleCancel(true)
        window.$message.success('保存成功')
      })
    }
  })
}
const title = computed(() => {
  return currentProject && currentProject.id ? '编辑项目' : '添加项目'
})
watch(() => currentProject, (p) => {
  if (p) {
    model.value.id = p.id
    model.value.name = p.name
    model.value.description = p.description
    model.value.enable = p.enable
    model.value.num = p.num
  }
})
</script>

<template>
  <w-modal v-model:show-modal="showModal" :title="title" @cancel="handleCancel(false)"
           @submit="handleSubmit">
    <template #content>
      <n-form
          ref="formRef"
          :model="model"
          :rules="rules"
          label-placement="left"
          label-width="auto"
          require-mark-placement="right-hanging"
      >
        <n-form-item label="项目名称" path="name">
          <n-input v-model:value="model.name" placeholder="请输入项目名称"/>
        </n-form-item>
        <n-form-item label="项目编号" path="num">
          <n-input v-model:value="model.num" placeholder="请输入项目编号"/>
        </n-form-item>
        <n-form-item label="项目描述" path="description">
          <n-input v-model:value="model.description" type="textarea" placeholder="请输入项目描述"/>
        </n-form-item>
      </n-form>
    </template>
  </w-modal>
</template>

<style scoped>

</style>