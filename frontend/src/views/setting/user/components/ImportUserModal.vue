<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {NAlert, NPopover, type UploadFileInfo} from "naive-ui";
import WUpload from '/@/components/w-upload/index.vue'
import {ref} from "vue";
import {userApi} from "/@/api/methods/user.ts";
import {useRequest} from "alova/client";

const {t} = useI18n()
const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false});
const importResultTitle = ref(t('system.user.importSuccessTitle'));

const importResult = ref<'success' | 'allFail' | 'fail'>('allFail');
const importSuccessCount = ref(0);
const importFailCount = ref(0);
const importErrorMessages = ref<Record<string, any>>({});
const userImportFile = ref<UploadFileInfo[]>([]);
const importResultVisible = ref(false);
const importErrorMessageDrawerVisible = ref(false);
const emit = defineEmits(['fetchDate']);
const {send: uploadFile} = useRequest((fileList) => userApi.importUserInfo(fileList), {immediate: false})
const handleImportUser = () => {
  uploadFile({fileList: [userImportFile.value[0]?.file]}).then(res => {
    const failCount = res.importCount - res.successCount;
    if (failCount === res.importCount) {
      importResult.value = 'allFail';
    } else if (failCount > 0) {
      importResult.value = 'fail';
    } else {
      importResult.value = 'success';
    }
    importSuccessCount.value = res.successCount;
    importFailCount.value = failCount;
    importErrorMessages.value = res.errorMessages;
    showImportResult();
  })
}
const showImportResult = () => {
  showModal.value = false;
  switch (importResult.value) {
    case 'success':
      importResultTitle.value = t('system.user.importSuccessTitle');
      emit('fetchDate')
      break;
    case 'allFail':
      importResultTitle.value = t('system.user.importModalTitle');
      break;
    case 'fail':
      importResultTitle.value = t('system.user.importModalTitle');
      emit('fetchDate')
      break;
    default:
      break;
  }
  importResultVisible.value = true;
}
const handleCancelImport = () => {
  showModal.value = false;
  importResultVisible.value = false;
  userImportFile.value = [];
}
const handleContinueImport = () => {
  importResultVisible.value = false;
  userImportFile.value = [];
  showModal.value = true;
}
const {send} = useRequest(() => userApi.downloadTemplate(), {
  immediate: false,
})

const downloadTemplate = () => {
  send().then(async (response: any) => {
    let fileName = 'export.xlsx'; // 默认文件名
    const blob = new Blob([response], {type: response.type});
    // const blob = await response.blob();
    // 创建临时 URL
    const url = URL.createObjectURL(blob);
    // 创建隐藏的 <a> 标签并触发点击
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;
    document.body.appendChild(link);
    link.click();

    // 清理
    document.body.removeChild(link);
    URL.revokeObjectURL(url); // 释放内存
  });
}
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" title="Dialog" @close="userImportFile = []">
    <template #header>
      <div>{{ t('system.user.importModalTitle') }}</div>
    </template>
    <n-alert class="mb-[16px]">
      {{ t('system.user.importModalTip') }}
      <n-button type="info" text @click="downloadTemplate">{{ t('system.user.importDownload') }}</n-button>
    </n-alert>
    <w-upload v-model:file-list="userImportFile" accept="excel" :max="1" :show-file-list="false"
              :auto-upload="false" size-unit="MB"/>
    <template #action>
      <n-button secondary>{{ t('system.user.importModalCancel') }}</n-button>
      <n-button type="primary" :disabled="userImportFile.length === 0" @click="handleImportUser">
        {{ t('system.user.importModalConfirm') }}
      </n-button>
    </template>
  </n-modal>
  <!--  导入结果信息弹框-->
  <n-modal v-model:show="importResultVisible" preset="dialog" title="Dialog" :closable="false">
    <template #header>
      <div>{{ importResultTitle }}</div>
    </template>
    <n-alert v-if="importResult === 'success'" type="success" :show-icon="false">
      <div class="flex flex-col items-center justify-center">
        <div class="i-solar:check-circle-bold-duotone text-[32px] text-green-6"/>
        <div class="text-[16px] font-medium leading-[24px]">
          {{ t('system.user.importSuccess') }}
        </div>
        <div class="sub-text">
          {{ t('system.user.importResultSuccessContent', {successNum: importSuccessCount}) }}
        </div>
      </div>
    </n-alert>
    <n-alert v-else type="error">
      <n-flex>
        {{ t('system.user.importResultContent', {successNum: importSuccessCount}) }}
        <div class="mx-[4px] text-red-6">{{ importFailCount }}</div>
        {{ t('system.user.importResultContentEnd') }}
        <n-popover trigger="hover" placement="bottom">
          <template #trigger>
            <n-button v-if="Object.keys(importErrorMessages).length > 0">
              {{ t('system.user.importErrorDetail') }}
            </n-button>
          </template>
          <div class="px-[16px] pt-[16px] text-[14px]">
            <n-flex class="flex items-center font-medium">
              <div>{{ t('system.user.importErrorMessageTitle') }}</div>
              <div class="ml-[4px]">({{ importFailCount }})</div>
            </n-flex>
            <div class="import-error-message-list mt-[8px]">
              <n-flex
                  v-for="key of Object.keys(importErrorMessages)"
                  :key="key"
                  class="mb-[16px]"
              >
                {{ t('system.user.num') }}
                <div class="mx-[4px] font-medium">{{ key }}</div>
                {{ t('system.user.line') }}：
                {{ importErrorMessages[key] }}
              </n-flex>
            </div>
          </div>
          <div v-if="Object.keys(importErrorMessages).length > 8" class="import-error-message-footer">
            <n-button text @click="importErrorMessageDrawerVisible = true">
              {{ t('system.user.seeMore') }}
            </n-button>
          </div>
        </n-popover>
      </n-flex>
    </n-alert>

    <template #action>
      <n-button text @click="handleCancelImport">{{ t('system.user.importResultReturn') }}</n-button>
      <n-button text @click="handleContinueImport">
        {{ t('system.user.importResultContinue') }}
      </n-button>
    </template>
  </n-modal>
</template>

<style scoped>
.import-error-message-footer {
  @apply flex items-center justify-center;

  padding: 8px 0;
  box-shadow: 0 -1px 4px 0 rgb(31 35 41 / 10%);
}

.sub-text {
  font-size: 12px;
  line-height: 16px;
}
</style>