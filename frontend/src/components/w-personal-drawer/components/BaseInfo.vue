<script setup lang="ts">
import {ref} from "vue";
import {useI18n} from "vue-i18n";

const {t}=useI18n()
const isEdit = ref(false);
</script>

<template>
  <div class="flex h-full flex-col overflow-hidden">
    <n-flex class="mb-[16px]" justify="space-between">
      <div class="font-medium">{{ t('ms.personal.baseInfo') }}</div>
      <n-button v-if="!isEdit" type="info" size="tiny" class="p-[2px_8px]" @click="isEdit = true">
        {{ t('common.update') }}
      </n-button>
    </n-flex>
    <n-flex class="=mb-[16px]">
      <div>x</div>
      <n-button size="tiny">{{ t('ms.personal.changeAvatar') }}</n-button>
    </n-flex>
  </div>
</template>

<style scoped>

</style>