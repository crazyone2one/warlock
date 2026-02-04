<script setup lang="ts">
import {computed, ref, watch} from "vue";
import type {IProjectItem} from "/@/api/types.ts";
import type {FormInst, FormRules} from "naive-ui";
import {useForm} from "alova/client";
import {projectApi} from "/@/api/methods/project.ts";
import {useAppStore} from "/@/store";
import {useI18n} from "vue-i18n";

const appStore = useAppStore()
const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false});
const {
  currentProject = {description: "", enable: true, name: "", id: '', num: ''}
} = defineProps<{ currentProject?: IProjectItem | null; }>();
const {t} = useI18n()
const formRef = ref<FormInst | null>(null)
const isEdit = computed(() => !!currentProject?.id);
const emit = defineEmits<{
  (e: 'cancel', shouldSearch: boolean): void;
}>();
const {form: model, reset, send, loading} = useForm(formData => projectApi.createOrUpdateProject(formData), {
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
  name: [{required: true, message: t('system.project.projectNameRequired')},
    {max: 255, message: t('common.nameIsTooLang')},],
  num: [{required: true, message: t('system.project.projectNumRequired')},
    {max: 255, message: t('common.nameIsTooLang')},],
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
        appStore.initProjectList()
      })
    }
  })
}
const title = computed(() => {
  return currentProject && currentProject.id ? t('common.update') : t('common.create')
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
  <n-modal v-model:show="showModal" preset="dialog" :title="title" :mask-closable="false"
           @cancel="handleCancel">
    <template #header>
      <div>
        <span v-if="isEdit">
        {{ t('system.project.updateProject') }}
        <span class="text-[#9597a4]">({{ currentProject?.name }})</span>
      </span>
        <span v-else>
        {{ t('system.project.createProject') }}
      </span>
      </div>
    </template>
    <n-form
        ref="formRef"
        :model="model"
        :rules="rules"
        label-placement="left"
        label-width="auto"
        require-mark-placement="right-hanging"
    >
      <n-form-item :label="t('system.project.name')" path="name">
        <n-input v-model:value="model.name" :placeholder="t('system.project.projectNamePlaceholder')"/>
      </n-form-item>
      <n-form-item :label="t('system.project.num')" path="num">
        <n-input v-model:value="model.num" :placeholder="t('system.project.projectNumPlaceholder')"/>
      </n-form-item>
      <n-form-item :label="t('common.desc')" path="description">
        <n-input v-model:value="model.description" type="textarea"
                 :placeholder="t('system.project.descriptionPlaceholder')"
                 clearable/>
      </n-form-item>
    </n-form>
    <template #action>
      <n-flex>
        <n-button secondary size="small" :loading="loading" @click="handleCancel">
          {{ t('common.cancel') }}
        </n-button>
        <n-button type="primary" size="small" :loading="loading" :disabled="loading"
                  @click="handleSubmit">
          {{ isEdit ? t('common.update') : t('common.create') }}
        </n-button>
      </n-flex>
    </template>
  </n-modal>
</template>

<style scoped>

</style>