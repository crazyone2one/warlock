<script setup lang="ts">

import {useRequest} from "alova/client";
import {scheduleApi} from "/@/api/methods/schedule.ts";
import {ref, watch} from "vue";
import type {IScheduleConfig, IScheduleInfo} from "/@/api/types.ts";
import EditRunConfigModal from "/@/views/schedule/components/EditRunConfigModal.vue";

const show = defineModel<boolean>('showModal', {type: Boolean, default: false});
const scheduleId = defineModel<string>('scheduleId', {type: String, default: ''});
const currentSchedule = ref<IScheduleInfo>({
  id: "", name: "", num: 0, projectName: "",
  jobKey: '',
  value: '',
  enable: true,
  runConfig: {jobKey: '', cron: '', enable: true, runConfig: {}}
})
const scheduleConfig = ref<IScheduleConfig>({jobKey: '', cron: '', enable: true, runConfig: {}})
const runConfig = ref<Record<string, string>>()
const showRunConfigModal = ref<boolean>(false)

const {send: fetchScheduleInfo} = useRequest((id) => scheduleApi.getScheduleInfo(id), {immediate: false});
const {send: updateScheduleConfig} = useRequest(data => scheduleApi.updateScheduleConfig(data), {immediate: false})
const handleUpdateConfig = (config: Record<string, string>) => {
  scheduleConfig.value = {
    jobKey: currentSchedule.value.jobKey,
    cron: currentSchedule.value.value,
    enable: currentSchedule.value.enable,
    runConfig: config
  }
  // 用于回显
  runConfig.value = config
}
const handleSave = () => {
  updateScheduleConfig(scheduleConfig.value).then(() => {
    show.value = false
    window.$message.success('保存成功')
  })
}
const handleCancel = () => {
  show.value = false
}
watch(() => show.value, (newValue) => {
  if (newValue) {
    fetchScheduleInfo(scheduleId.value).then((resp) => {
      currentSchedule.value = resp
      // scheduleConfig.value = resp.runConfig
      runConfig.value = resp.runConfig?.runConfig
    })
  }
})
</script>

<template>
  <n-drawer v-model:show="show" :width="502" :mask-closable="false" :close-on-esc="false">
    <n-drawer-content :title="`定时任务配置-${currentSchedule?.name}`" :native-scrollbar="false">
      <n-flex vertical>
        <div>
          <n-button secondary type="info" @click="showRunConfigModal = true">编辑运行参数</n-button>
        </div>
        {{ currentSchedule?.runConfig || runConfig }}
      </n-flex>
      <template #footer>
        <n-flex>
          <n-button secondary @click="handleCancel">cancel</n-button>
          <n-button type="primary" @click="handleSave">save</n-button>
        </n-flex>
      </template>
    </n-drawer-content>
  </n-drawer>
  <edit-run-config-modal v-model:show-modal="showRunConfigModal" v-model:run-config="runConfig"
                         @update-config="handleUpdateConfig"/>
</template>

<style scoped>

</style>