<script setup lang="ts">
import {ref} from "vue";

const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false});
const {title = '', projectName = '', subTitle = '输入需要删除的项目名称'} = defineProps<{
  title?: string,
  projectName: string,
  subTitle?: string
}>()
const currentValue = ref('')
const emit = defineEmits<{
  (e: 'cancel'): void;
  (e: 'submit'): void;
}>();
const handleSubmit = () => {
  if (currentValue.value === projectName) {
    emit('submit');
  } else {
    window.$message.warning('项目名称错误')
  }
}
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" title="Dialog" :mask-closable="false"
           @cancel="emit('cancel')">
    <template #header>
      <div>
        {{ title }}
      </div>
    </template>
    <n-flex vertical>
      {{ subTitle }}
      <n-input v-model:value="currentValue"/>
    </n-flex>
    <template #action>
      <n-flex>
        <n-button secondary size="small" @click="emit('cancel')">取消</n-button>
        <n-button type="primary" size="small" @click="handleSubmit">确定</n-button>
      </n-flex>
    </template>
  </n-modal>
</template>

<style scoped>

</style>