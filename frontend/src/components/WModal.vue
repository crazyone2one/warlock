<script setup lang="ts">
const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false});
const {title = ''} = defineProps<{ title?: string }>()
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
      <slot name="content"></slot>
    </div>
    <template #action>
      <n-flex>
        <slot name="action"></slot>
        <div>
          <n-flex>
            <n-button secondary size="small" @click="emit('cancel')">取消</n-button>
            <n-button type="primary" size="small" @click="emit('submit')">确定</n-button>
          </n-flex>
        </div>
      </n-flex>
    </template>
  </n-modal>
</template>

<style scoped>

</style>