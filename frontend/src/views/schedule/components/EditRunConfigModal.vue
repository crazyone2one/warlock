<script setup lang="ts">
import {type FormInst, NScrollbar} from "naive-ui";
import {reactive, ref, watchEffect} from "vue";

const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false});
const runConfig = defineModel<Record<string, string>>('runConfig', {type: Object, default: () => ({})});
const emit = defineEmits<{
  change: [id: number]
  updateConfig: [value: Record<string, string>]
}>()
const formRef = ref<FormInst | null>(null)
const dynamicForm = reactive({
  runConfig: [{key: '', value: ''}]
})
const addItem = () => {
  dynamicForm.runConfig.push({key: '', value: ''})
}
const removeItem = (index: number) => {
  dynamicForm.runConfig.splice(index, 1)
}
const handleCancel = () => {
  showModal.value = false
  dynamicForm.runConfig = [{key: '', value: ''}]
}
const handleSave = () => {
  formRef.value?.validate().then(() => {
    const config = Object.fromEntries(dynamicForm.runConfig.map(item => [item.key, item.value]));
    emit('updateConfig', config)
    handleCancel()
  })
}
watchEffect(() => {
  if (runConfig.value && Object.keys(runConfig.value).length > 0) {
    dynamicForm.runConfig = Object.entries(runConfig.value).map(([key, value]) => ({key, value}))
  } else {
    dynamicForm.runConfig = [{key: '', value: ''}]
  }
})
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" title="Dialog">
    <template #header>
      <div>
        自定义参数
      </div>
    </template>
    <div>
      <n-button size="small" tertiary color="#ff69b4" @click="addItem">
        <template #icon>
          <n-icon>
            <div class="i-solar:add-circle-linear "/>
          </n-icon>
        </template>
        Add Item
      </n-button>
      <div class="p-4">
        <n-form ref="formRef" :model="dynamicForm" label-placement="left" size="small">
          <n-scrollbar style="max-height: 220px">
            <n-form-item v-for="(item, index) in dynamicForm.runConfig" :key="index"
                         :label="`param ${index + 1}`" class="px-4">
              <div class="w-[160px]">
                <n-input v-model:value="item.key" placeholder="Key" clearable/>
              </div>
              <n-input v-model:value="item.value" placeholder="Value" clearable class="ml-1"/>
              <n-button v-show="dynamicForm.runConfig.length>1" text class="ml-1"
                        @click="removeItem(index)">
                <template #icon>
                  <n-icon>
                    <div class="i-solar:trash-bin-trash-linear "/>
                  </n-icon>
                </template>
              </n-button>
            </n-form-item>
          </n-scrollbar>
        </n-form>
      </div>

    </div>
    <template #action>
      <n-button secondary @click="handleCancel">cancel</n-button>
      <n-button type="primary" @click="handleSave">save</n-button>
    </template>
  </n-modal>
</template>

<style scoped>

</style>