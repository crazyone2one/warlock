<script setup lang="ts">

import ModuleTree from "/@/views/api-test/management/components/ModuleTree.vue";
import {ref} from "vue";
import type {TreeOption} from "naive-ui";
import Management from "/@/views/api-test/management/components/management/index.vue";
import type {ModuleTreeNode} from "/@/api/types/api-test.ts";

const folderTree = ref<ModuleTreeNode[]>([]);
const activeModule = ref<string>('all');
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
        <Management :offspring-ids="offspringIds" :module-tree="folderTree" :active-module="activeModule"/>
      </template>
    </n-split>
  </n-card>
</template>

<style scoped>

</style>