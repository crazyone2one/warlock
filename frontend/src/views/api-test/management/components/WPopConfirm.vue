<script setup lang="ts">
import {type FormInst, type FormRules, NPopconfirm} from "naive-ui";
import {useI18n} from "vue-i18n";
import {useAppStore} from "/@/store";
import {ref} from "vue";

interface FieldConfig {
  field?: string;
  rules?: FormRules;
  placeholder?: string;
  maxLength?: number;
  isTextArea?: boolean;
}

const {t} = useI18n()
const appStore = useAppStore()
const show = defineModel<boolean>('show', {type: Boolean, default: false});
const props = defineProps<{
  mode: 'add' | 'rename' | 'tabRename';
  nodeType?: 'MODULE' | 'API';
  title?: string;
  allNames: string[];
  parentId?: string; // 父节点 id
  nodeId?: string; // 节点 id
  fieldConfig?: FieldConfig;
  addModuleApi?: (params: { projectId: string; parentId: string; name: string }) => Promise<any>;
  updateModuleApi?: (params: { id: string; name: string }) => Promise<any>;
  repeatMessage?: string;
}>();
const emit = defineEmits(['update:visible', 'close', 'addFinish', 'renameFinish']);
const formRef = ref<FormInst | null>(null)
const form = ref({
  field: props.fieldConfig?.field || '',
});
const rules = {
  field: [{required: true, message: t('common.nameNotNull'), trigger: 'blur'},
    {
      validator(_rule: any, value: string) {
        if (props.allNames.includes(value)) {
          return new Error(props.repeatMessage || t('project.fileManagement.nameExist'))
        }
      },
    }]
}
const handlePositiveClick = (done?: (closed: boolean) => void) => {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      if (props.mode === 'add' && props.addModuleApi) {
        // 添加根级模块
        await props.addModuleApi({
          projectId: appStore.currentProjectId,
          parentId: props.parentId || '',
          name: form.value.field,
        });
        window.$message.success(t('common.addSuccess'));
        emit('addFinish', form.value.field);
      } else if (props.mode === 'rename' && props.updateModuleApi) {
        // 模块重命名
        await props.updateModuleApi({
          id: props.nodeId || '',
          name: form.value.field,
        });
        window.$message.success(t('common.updateSuccess'));
        emit('renameFinish', form.value.field, props.nodeId);
        if (done) {
          done(true);
        } else {
          handleNegativeClick(false)
        }
      }
    } else if (done) {
      done(false);
    }
  })
}
const handleNegativeClick = (val: boolean) => {
  if (!val) {
    show.value = false
    form.value.field = '';
  }
}
</script>

<template>
  <n-popconfirm
      v-model:show="show" placement="bottom"
      :show-icon="false"
      @positive-click="handlePositiveClick()"
      @negative-click="handleNegativeClick(false)"
  >
    <template #trigger>
      <slot></slot>
    </template>
    <n-flex vertical>
      <div class="font-medium">
        {{ props.title || (props.mode === 'add' ? t('project.fileManagement.addSubModule') : t('common.rename')) }}
      </div>
      <n-form ref="formRef" v-model:model="form" :rules="rules">
        <n-form-item path="field">
          <n-input v-model:value="form.field" :placeholder="t('common.namePlaceholder')"/>
        </n-form-item>
      </n-form>
    </n-flex>
  </n-popconfirm>
</template>

<style scoped>

</style>