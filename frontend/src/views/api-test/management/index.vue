<script setup lang="ts">

import ModuleTree from "/@/views/api-test/management/components/ModuleTree.vue";
import {ref} from "vue";
import type {TreeOption} from "naive-ui";

const offspringIds = ref<string[]>([]);
const moduleTreeRef = ref<InstanceType<typeof ModuleTree>>();
const handleNodeSelect = (option: TreeOption, _offspringIds: string[]) => {
  console.log(option)
  offspringIds.value = _offspringIds;
}
</script>

<template>
  <n-card>
    <n-split direction="horizontal" :max="0.5" :default-size="0.2">
      <template #1>
        <module-tree ref="moduleTreeRef" @folder-node-select="handleNodeSelect"/>
      </template>
      <template #2>
        Pane 2
      </template>
    </n-split>
  </n-card>
</template>

<style scoped>

</style>