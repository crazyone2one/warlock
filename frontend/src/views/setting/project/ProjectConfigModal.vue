<script setup lang="ts">
import type {FormInst} from "naive-ui";
import {ref, watchEffect} from "vue";
import type {IProjectItem} from "/@/api/types.ts";
import {useForm, useRequest} from "alova/client";
import {projectParameterApi} from "/@/api/methods/project-parameter.ts";
import WTip from "/@/components/WTip.vue";

const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false});
const {
  currentProject = {name: "", id: '', num: ''}
} = defineProps<{ currentProject?: IProjectItem; }>();
const bodyStyle = {width: '600px'}
const activeTab = ref('ssh')
const formRef = ref<FormInst | null>(null)
const handleChangeTab = (value: string) => {
  activeTab.value = value
}
const rules = {
  parameters: {
    host: {required: true, message: '请输入IP', trigger: ['input', 'blur']},
    port: {required: true, message: '请输入PORT', trigger: ['input', 'blur']},
    username: {required: true, message: '请输入Username', trigger: ['input', 'blur']},
    password: {required: true, message: '请输入Password', trigger: ['input', 'blur']},
    localPath: {required: true, message: '请输入Local path', trigger: ['input', 'blur']},
    remotePath: {required: true, message: '请输入Remote path', trigger: ['input', 'blur']},
    name: {required: true, message: '请输入名称', trigger: ['input', 'blur']},
    url: {required: true, message: '请输入URL', trigger: ['input', 'blur']},
  }
}
const {
  form,
  loading,
  send: submit,
  reset
} = useForm(formData => formData.id ? projectParameterApi.updateParameter(formData) : projectParameterApi.addParameter(formData), {
  initialForm: {
    id: '',
    projectId: '',
    parameterType: activeTab.value === 'ssh' ? 'ssh' : 'datasource',
    parameters: activeTab.value === 'ssh' ? {
          host: '',
          port: '',
          localPath: '',
          remotePath: '',
          username: '',
          password: ''
        }
        : {name: '', url: '', username: '', password: ''}
  },
  resetAfterSubmiting: true
})
const handleSubmit = () => {
  formRef.value?.validate((error) => {
    if (!error) {
      form.value.projectId = currentProject.id
      form.value.parameterType = activeTab.value === 'ssh' ? 'ssh' : 'datasource'
      if (activeTab.value === 'datasource') {
        const tmpUrl = form.value.parameters.url
        form.value.parameters.url = `jdbc:mysql://${tmpUrl}?allowPublicKeyRetrieval=true&allowMultiQueries=true&useUnicode=true&useSSL=false&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&autoReconnect=true&nullCatalogMeansCurrent=true`
      }
      submit().then(() => {
        // showModal.value = false
        window.$message.success('保存成功')
      });
    }
  })
}
const handleCancel = () => {
  showModal.value = false
  formRef.value?.restoreValidation()
  reset()
  activeTab.value = 'ssh'
}
const {send: fetchParameter} = useRequest((projectId, type) => projectParameterApi.getParameter(projectId, type), {immediate: false})

const match = (jdbcUrl: string) => {
  const match = jdbcUrl.match(/jdbc:mysql:\/\/([^?]+)/);
  return match ? match[1] : '';
}
watchEffect(() => {
  if (showModal) {
    if (currentProject) {
      fetchParameter(currentProject.id, activeTab.value).then(res => {
        if (res) {
          form.value = res;
          if ("url" in res.parameters && res.parameters.url) {
            form.value.parameters.url = match(res.parameters.url);
          }
          form.value = res;
        }
      })
    }
  }
})
</script>

<template>
  <n-modal
      v-model:show="showModal"
      preset="dialog"
      :style="bodyStyle"
      :title="`项目参数配置`"
      :bordered="false"
      :auto-focus="false"
      :loading="loading"
      @close="handleCancel"
  >
    <template #header-extra>
    </template>
    <n-tabs v-model:value="activeTab" justify-content="space-evenly" type="line"
            @update:value="handleChangeTab">
      <n-tab-pane name="ssh" tab="Ssh Slave">
        <n-form ref="formRef" :model="form" :rules="rules" :style="{ maxWidth: '640px' }"
                :size="'small'"
                label-placement="left"
                label-width="auto"
                require-mark-placement="right-hanging">
          <n-grid :cols="12" :x-gap="12">
            <n-form-item-gi :span="6" label="Host" path="parameters.host">
              <n-input v-model:value="form.parameters.host" clearable placeholder="输入远程IP"/>
            </n-form-item-gi>
            <n-form-item-gi :span="6" label="Port" path="parameters.port">
              <n-input v-model:value="form.parameters.port" clearable placeholder="默认端口：22"/>
            </n-form-item-gi>
            <n-form-item-gi :span="6" label="Username" path="parameters.username">
              <n-input v-model:value="form.parameters.username" clearable placeholder="输入远程username"/>
            </n-form-item-gi>
            <n-form-item-gi :span="6" label="Password" path="parameters.password">
              <n-input v-model:value="form.parameters.password" clearable placeholder="输入远程password"/>
            </n-form-item-gi>
            <n-form-item-gi :span="12" label="Local path" path="parameters.localPath">
              <n-input v-model:value="form.parameters.localPath" clearable placeholder="输入本地路径，/home/app"/>
            </n-form-item-gi>
            <n-form-item-gi :span="12" label="Remote path" path="parameters.remotePath">
              <n-input v-model:value="form.parameters.remotePath" clearable
                       placeholder="输入远程服务器路径，/home/app"/>
            </n-form-item-gi>
          </n-grid>

        </n-form>
      </n-tab-pane>
      <n-tab-pane name="datasource" tab="Datasource Slave">
        <n-form ref="formRef" :model="form" :rules="rules" :style="{ maxWidth: '640px' }"
                :size="'small'"
                label-placement="top"
                label-width="auto"
                require-mark-placement="right-hanging">
          <n-grid :cols="12" :x-gap="12">
            <n-form-item-gi :span="12" label="Name" path="parameters.name">
              <n-input v-model:value="form.parameters.name" clearable placeholder="输入节点名称"/>
              <w-tip content="ds节点名称：ds-slave+项目编号"/>
            </n-form-item-gi>
            <n-form-item-gi :span="12" label="URL" path="parameters.url">
              <n-input v-model:value="form.parameters.url" clearable placeholder="输入URL,127.0.0.1:3306/database">
                <template #prefix>
                  <span class="bg-gray">jdbc:mysql://</span>
                </template>
              </n-input>
              <w-tip content="数据库信息：ip + port + database,如 127.0.0.1:3306/database"/>
            </n-form-item-gi>
            <n-form-item-gi :span="6" label="Username" path="parameters.username">
              <n-input v-model:value="form.parameters.username" clearable placeholder="输入Username"/>
              <w-tip content="数据库user"/>
            </n-form-item-gi>
            <n-form-item-gi :span="6" label="Password" path="parameters.password">
              <n-input v-model:value="form.parameters.password" clearable placeholder="输入Password"/>
              <w-tip content="数据库password"/>
            </n-form-item-gi>
          </n-grid>
        </n-form>
      </n-tab-pane>

    </n-tabs>
    <template #action>
      <n-button secondary @click="handleCancel">取消</n-button>
      <n-button type="primary" @click="handleSubmit">确定</n-button>
    </template>
  </n-modal>
</template>

<style scoped>

</style>