<script setup lang="ts">
import PopConfirm from '/@/components/w-popconfirm/index.vue';
import {ref} from "vue";
import {useI18n} from "vue-i18n";

const props = withDefaults(
    defineProps<{
      title: string;
      subTitleTip: string;
      loading?: boolean;
      removeText?: string;
      okText?: string;
      disabled?: boolean;
    }>(),
    {
      removeText: 'common.remove',
      disabled: false,
    }
);
const {t} = useI18n();
const currentVisible = ref(false);
const emit = defineEmits<{
  (e: 'ok'): void;
}>();
const handleOk = () => {
  emit('ok');
};
const handleCancel = () => {
  currentVisible.value = false;
};

const showPopover = () => {
  currentVisible.value = true;
};
</script>

<template>
<span>
  <pop-confirm :title="props.title"
               :sub-title-tip="props.subTitleTip"
               :loading="props.loading"
               v-model:show="currentVisible"
               :ok-text="props.okText"
               @confirm="handleOk"
               @cancel="handleCancel">
    <slot>
      <n-button type="primary" text :disabled="props.disabled" @click="showPopover">
        {{ t(props.removeText) }}
      </n-button>
    </slot>
  </pop-confirm>
</span>
</template>

<style scoped>

</style>