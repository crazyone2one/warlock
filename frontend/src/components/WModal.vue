<script setup lang="ts">
import {useI18n} from "vue-i18n";

const {t} = useI18n()
const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false});
const {title = '', okText = 'common.confirm', loading = false, disabled = false} = defineProps<
    { title?: string, okText?: string, loading?: boolean, disabled?: boolean }
>()
const emit = defineEmits<{
  (e: 'cancel'): void;
  (e: 'submit'): void;
}>();

</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" title="Dialog" :mask-closable="false"
           @cancel="emit('cancel')">
    <template #header>
      <div>
        <slot name="header"></slot>
        {{ title }}
      </div>
    </template>
    <div>
      <slot></slot>
    </div>
    <template #action>
      <n-flex>
        <slot name="action"></slot>
        <div>
          <n-flex>
            <n-button secondary size="small" :loading="loading" @click="emit('cancel')">
              {{ t('common.cancel') }}
            </n-button>
            <n-button type="primary" size="small" :loading="loading" :disabled="disabled"
                      @click="emit('submit')">
              {{ t(okText) }}
            </n-button>
          </n-flex>
        </div>
      </n-flex>
    </template>
  </n-modal>
</template>

<style scoped>

</style>