<script setup lang="ts">
import type {FormInst} from "naive-ui";
import {reactive, ref} from "vue";
import type {IProjectItem} from "/@/api/types.ts";
import {useForm} from "alova/client";
import {projectParameterApi} from "/@/api/methods/project-parameter.ts";

const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false});
const {
  currentProject = {name: "", id: '', num: ''}
} = defineProps<{ currentProject?: IProjectItem; }>();
const bodyStyle = {width: '600px'}
const activeTab = ref('sshSlave')
const sshFormRef = ref<FormInst | null>(null)
const handleChangeTab = (value: string) => {
  activeTab.value = value
}
const datasourceFormRef = ref<FormInst | null>(null)

const datasourceForm = reactive({
  name: '',
})

const transferRules = {
  parameters: {
    host: {required: true, message: '请输入IP', trigger: ['input', 'blur']},
    port: {required: true, message: '请输入PORT', trigger: ['input', 'blur']},
    username: {required: true, message: '请输入Username', trigger: ['input', 'blur']},
    password: {required: true, message: '请输入Password', trigger: ['input', 'blur']},
    localPath: {required: true, message: '请输入Local path', trigger: ['input', 'blur']},
    remotePath: {required: true, message: '请输入Remote path', trigger: ['input', 'blur']},
  }
}
const {
  form: sshForm,
  loading: sshLoading,
  send: sshSubmit
} = useForm(formData => projectParameterApi.addOrUpdate(formData), {
  initialForm: {
    id: '',
    projectId: '',
    parameterType: 'ssh',
    parameters: {
      host: '', port: '', localPath: '', remotePath: '', username: '', password: ''
    }
  }
})
const handleSubmit = () => {
  sshFormRef.value?.validate((error) => {
    if (!error) {
      sshForm.value.projectId = currentProject.id
      sshSubmit().then(() => {
        showModal.value = false
        console.log('submit')
      })
    }
  })
}
</script>

<template>
  <n-modal
      v-model:show="showModal"
      preset="dialog"
      :style="bodyStyle"
      :title="`项目参数配置`"
      size="huge"
      :bordered="false"
      :auto-focus="false"
      :loading="sshLoading"
  >
    <template #header-extra>
    </template>
    <n-tabs v-model:value="activeTab" justify-content="space-evenly" type="line"
            @update:value="handleChangeTab">
      <n-tab-pane name="sshSlave" tab="Ssh Slave">
        <n-form ref="sshFormRef" :model="sshForm" :rules="transferRules" :style="{ maxWidth: '640px' }"
                :size="'small'"
                label-placement="left"
                label-width="auto"
                require-mark-placement="right-hanging">
          <n-grid :cols="12" :x-gap="12">
            <n-form-item-gi :span="6" label="Host" path="parameters.host">
              <n-input v-model:value="sshForm.parameters.host" clearable placeholder="输入远程IP"/>
            </n-form-item-gi>
            <n-form-item-gi :span="6" label="Port" path="parameters.port">
              <n-input v-model:value="sshForm.parameters.port" clearable placeholder="默认端口：22"/>
            </n-form-item-gi>
            <n-form-item-gi :span="6" label="Username" path="parameters.username">
              <n-input v-model:value="sshForm.parameters.username" clearable placeholder="输入远程username"/>
            </n-form-item-gi>
            <n-form-item-gi :span="6" label="Password" path="parameters.password">
              <n-input v-model:value="sshForm.parameters.password" clearable placeholder="输入远程password"/>
            </n-form-item-gi>
            <n-form-item-gi :span="12" label="Local path" path="parameters.localPath">
              <n-input v-model:value="sshForm.parameters.localPath" clearable placeholder="输入本地路径，/home/app"/>
            </n-form-item-gi>
            <n-form-item-gi :span="12" label="Remote path" path="parameters.remotePath">
              <n-input v-model:value="sshForm.parameters.remotePath" clearable
                       placeholder="输入远程服务器路径，/home/app"/>
            </n-form-item-gi>
          </n-grid>

        </n-form>
      </n-tab-pane>
      <n-tab-pane name="datasourceSlave" tab="Datasource Slave">
        Wonderwall
      </n-tab-pane>

    </n-tabs>
    <template #action>
      <n-button>btn1</n-button>
      <n-button type="primary" @click="handleSubmit">确定</n-button>
    </template>
  </n-modal>
</template>

<style scoped>

</style>