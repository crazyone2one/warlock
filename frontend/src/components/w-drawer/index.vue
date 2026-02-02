<script setup lang="ts">
import {NDrawer, NDrawerContent, NPopover} from "naive-ui";
import {useI18n} from "vue-i18n";

interface DrawerProps {
  title?: string | undefined;
  width: string | number; // 抽屉宽度，为数值时才可拖拽改变宽度
  closable?: boolean; // 是否显示右上角的关闭按钮
  maskClosable?: boolean; // 点击遮罩是否关闭
  okLoading?: boolean;
  okDisabled?: boolean;
  okPermission?: string[]; // 确认按钮权限
  okText?: string;
  cancelText?: string;
  saveContinueText?: string;
  showContinue?: boolean;
  footer?: boolean;
  showSkeleton?: boolean; // 是否显示骨架屏
}

const props = withDefaults(defineProps<DrawerProps>(), {
  footer: true,
  closable: true,
  showSkeleton: false,
  showContinue: false,
  maskClosable: true,
  okPermission: () => [], // 确认按钮权限
});
const show = defineModel<boolean>('show', {type: Boolean, default: false});
const emit = defineEmits(['confirm', 'cancel', 'continue', 'close']);
const {t} = useI18n();
const handleCancel = () => {
  show.value = false;
  emit('cancel');
}
</script>

<template>
  <n-drawer v-bind="props" v-model:show="show" :width="props.width">
    <n-drawer-content :closable="closable" :native-scrollbar="false">
      <template #header>
        <n-flex>
          <slot name="title">
            <n-flex>
              <n-flex>
                <n-popover :disabled="!props.title" trigger="hover">
                  <template #trigger>
                    <span class="one-line-text max-w-[300px]"> {{ props.title }}</span>
                  </template>
                  <span>{{ props.title }}</span>
                </n-popover>
                <slot name="headerLeft"></slot>
              </n-flex>
              <slot name="tbutton"></slot>
            </n-flex>
          </slot>
        </n-flex>
      </template>
      <slot></slot>
      <template #footer>
        <div v-if="footer">
          <slot name="footer">
            <n-flex>
              <slot name="footerLeft"></slot>
              <div class="ml-auto flex gap-[12px]">
                <n-button :disabled="props.okLoading" @click="handleCancel">{{
                    t(props.cancelText || 'ms.drawer.cancel')
                  }}
                </n-button>
                <n-button v-if="showContinue" v-permission="props.okPermission || []" secondary
                          :loading="props.okLoading" :disabled="okDisabled" @click="emit('continue');">
                  {{ t(props.saveContinueText || 'ms.drawer.saveContinue') }}
                </n-button>
                <n-button v-permission="props.okPermission || []" type="primary"
                          :loading="props.okLoading" :disabled="okDisabled" @click="emit('confirm');">
                  {{ t(props.okText || 'ms.drawer.ok') }}
                </n-button>
              </div>
            </n-flex>
          </slot>
        </div>
      </template>
    </n-drawer-content>
  </n-drawer>
</template>

<style scoped>

</style>