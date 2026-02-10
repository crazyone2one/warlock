<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {h, onBeforeMount, ref} from "vue";
import {NButton, NFlex, NTree, type TreeOption} from "naive-ui";
import type {ApiDefinitionGetModuleParams} from "/@/api/types/api-test.ts";
import {useAppStore} from "/@/store";
import {useRequest} from "alova/client";
import {apiTestManagementApi} from "/@/api/methods/api-test/management.ts";
import WPopConfirm from "/@/views/api-test/management/components/WPopConfirm.vue";


const {t} = useI18n()
const pattern = ref('')
const emit = defineEmits([
  'init',
  'newApi',
  'import',
  'folderNodeSelect',
  'clickApiNode',
  'changeProtocol',
  'updateApiNode',
  'deleteNode',
  'execute',
  'openCurrentNode',
  'exportShare',
]);
const appStore = useAppStore()
const lastModuleCountParam = ref<ApiDefinitionGetModuleParams>({
  projectId: appStore.currentProjectId,
  keyword: '',
  // protocols: selectedProtocols.value,
  moduleIds: [],
})
const {
  send: fetchModuleTree,
  data
} = useRequest(param => apiTestManagementApi.getModuleTree(param), {immediate: false});
const initModuleCount = (params: ApiDefinitionGetModuleParams) => {
  lastModuleCountParam.value = params;
  fetchModuleTree(params).then(res => {
    console.log(res)
  })
}
const renderLabel = ({option}: { option: TreeOption }) => {
  return h(NFlex, {class: 'w-full gap-[8px]'}, {
    default: () => [
      h('span', {}, {default: () => option.name}),
      h('span', {class: 'text-gray-500'}, {default: () => option.count || 0}),
    ]
  })
}
const renderSuffix = ({option}: { option: TreeOption }) => {
  return h(NFlex, {class: 'w-full gap-[8px]'}, {
    default: () => {
      const tmp = [];
      if (option.id !== 'root' && option.type === 'MODULE') {
        tmp.push(
            h(WPopConfirm, {
              mode: 'add',
              allNames: (option.children || []).map((child: any) => child.name),
              parentId: option.id as string,
              addModuleApi: apiTestManagementApi.addModule
            }, {
              default: () => h(NButton, {type: 'primary', size: 'tiny', text: true}, {
                default: () => h('div', {class: "i-solar:add-circle-linear"})
              })
            })
        )
      }
      if (option.id !== 'root') {
        tmp.push(
            h(WPopConfirm, {
              mode: 'rename',
              allNames: data.value.map((child: any) => child.name),
              parentId: option.id as string, nodeId: option.id as string
            }, {default: () => h('div', {class: 'relative h-full', id: `renameSpan${option.id}`})})
        )
      }
      return tmp
    }
  })
}
const nodeProps = ({option}: { option: TreeOption }) => {
  return {
    onClick() {
      const offspringIds: string[] = [];
      (option.children || []).forEach(child => {
        offspringIds.push(child.id as string)
      })
      emit('folderNodeSelect', option, offspringIds);
    }
  }
}
onBeforeMount(() => {
  initModuleCount(lastModuleCountParam.value)
})
</script>

<template>
  <div class="m-1">
    <n-flex vertical :size="12">
      <n-input v-model:value="pattern" :placeholder="t('apiTestManagement.searchTip')"/>
      <n-tree
          :show-irrelevant-nodes="false"
          :pattern="pattern"
          :data="data"
          label-field="name"
          key-field="id"
          block-line
          :render-label="renderLabel"
          :render-suffix="renderSuffix"
          :node-props="nodeProps"
      />
    </n-flex>
  </div>
</template>

<style scoped>

</style>