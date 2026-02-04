<script setup lang="ts">
import {nextTick, ref} from "vue";
import type {InputInst} from "naive-ui";

const isEdit = ref(false)
const inputRef = ref<InputInst | null>(null)
const inputValue = defineModel<string>('inputValue', {type: String, default: ''});
const emits = defineEmits<{
  (e: 'confirm', v: string): void;
}>();
const handleChange = () => {
  isEdit.value = false
  emits('confirm', inputValue.value)
}
const handleOnClick = () => {
  isEdit.value = true
  nextTick(() => {
    inputRef.value?.focus()
  })
}

</script>

<template>
  <div>
    <n-input v-if="isEdit" ref="inputRef" v-model:value="inputValue" @blur="handleChange"/>
    <span v-else @click="handleOnClick">{{ inputValue }}</span>
  </div>
</template>

<style scoped>

</style>