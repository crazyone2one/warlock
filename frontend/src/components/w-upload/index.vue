<script setup lang="ts">
import {NP, NText, NUpload, NUploadDragger, type UploadFileInfo} from "naive-ui";
import {UploadAcceptEnum} from "/@/utils/upload-enum.ts";
import {computed} from "vue";
import {useI18n} from "vue-i18n";
import {formatFileSize} from "/@/utils";
// 上传 组件 props
type UploadProps = Partial<{
  mainText: string; // 主要文案
  subText: string; // 次要文案
  showSubText: boolean; // 是否显示次要文案
  class: string;
  multiple: boolean;
  imagePreview: boolean;
  showFileList: boolean;
  disabled: boolean;
  iconType: string;
  maxSize?: number; // 文件大小限制，单位 MB
  sizeUnit: 'MB' | 'KB'; // 文件大小单位
  isLimit: boolean; // 是否限制文件大小
  draggable: boolean; // 是否支持拖拽上传
  isAllScreen?: boolean; // 是否是全屏显示拖拽上传
  fileTypeTip?: string; // 上传文件类型错误提示
  max: number; // 限制上传文件数量
  allowRepeat?: boolean; // 自定义上传文件框，是否允许重复文件名替换
}> & {
  accept: keyof typeof UploadAcceptEnum;
};
const props = withDefaults(defineProps<UploadProps>(), {
  showSubText: true,
  isLimit: true,
  isAllScreen: false,
  cutHeight: 110,
  allowRepeat: false,
  sizeUnit: 'MB',
  maxSize: 50
});
const {t} = useI18n()
const emit = defineEmits(['update:fileList', 'change']);
const innerFileList = defineModel<UploadFileInfo[]>('fileList', {
  default: () => [],
});
const acceptType = computed(() => {
  return ['none', 'unknown'].includes(UploadAcceptEnum[props.accept])
      ? '*'
      : UploadAcceptEnum[props.accept]
})
</script>

<template>
  <n-upload
      v-model:file-list="innerFileList"
      v-bind="{ ...props }"
      :accept="acceptType"
      :multiple="props.multiple"
      directory-dnd
  >
    <n-upload-dragger>
      <div v-if="innerFileList.length === 0 || props.multiple" class="ms-upload-area">
        <div style="margin-bottom: 12px">
          <n-icon size="48" :depth="3">
            <div class="i-solar:archive-up-minimlistic-broken"/>
          </n-icon>
        </div>
        <n-text style="font-size: 16px">
          {{ t(props.mainText || 'ms.upload.importModalDragText') }}
        </n-text>
        <n-p depth="3" style="margin: 8px 0 0 0">
          <slot name="subText">
            {{
              t(props.subText || 'ms.upload.importModalFileTip', {
                type: UploadAcceptEnum[props.accept],
                size: props.maxSize,
              })
            }}
          </slot>
        </n-p>
      </div>
      <div v-else>
        <span class="one-line-text w-[80%] text-center"> {{ innerFileList[0]?.name }}</span>
        <div class="ms-upload-sub-text">{{ formatFileSize(innerFileList[0]?.file?.size || 0) }}</div>
      </div>
    </n-upload-dragger>
  </n-upload>
</template>

<style scoped>
</style>