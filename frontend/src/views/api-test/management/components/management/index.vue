<script setup lang="ts">
import type {ModuleTreeNode} from "/@/api/types/api-test.ts";
import {hasAnyPermission} from "/@/utils/permission.ts";
import {useI18n} from "vue-i18n";
import WDataTableToolBar from "/@/components/WDataTableToolBar.vue";
import WPagination from "/@/components/WPagination.vue";
import {type DataTableColumns, NButton, NDivider, NFlex, NSelect} from "naive-ui";
import type {ApiDefinitionDetail} from "/@/api/types/api-test/management.ts";
import {computed, h, ref, withDirectives} from "vue";
import ApiStatus from "/@/views/api-test/components/ApiStatus.vue";
import permission from "/@/directive/permission";
import CreateOrEditDrawer from "/@/views/api-test/management/components/CreateOrEditDrawer.vue";

const props = defineProps<{
  activeModule: string;
  offspringIds: string[];
  selectedProtocols?: string[];
  moduleTree: ModuleTreeNode[]; // 模块树
}>();
const emit = defineEmits<{
  (e: 'import'): void;
  (e: 'handleAdvSearch', isStartAdvance: boolean): void;
}>();
const {t} = useI18n()
const showEditDrawer = ref(false)
const hasOperationPermission = computed(() =>
    hasAnyPermission([
      'PROJECT_API_DEFINITION:READ+DELETE',
      'PROJECT_API_DEFINITION:READ+ADD',
      'PROJECT_API_DEFINITION:READ+EXECUTE',
      'PROJECT_API_DEFINITION:READ+UPDATE',
    ])
);
const columns: DataTableColumns<ApiDefinitionDetail> = [
  {type: 'selection', fixed: 'left'},
  {
    title: "ID", key: 'num', render(row) {
      return h(NButton, {text: true}, {default: () => row.num})
    }
  },
  {title: () => t('apiTestManagement.apiName'), key: 'name', ellipsis: {tooltip: true}, width: 200},
  {title: () => t('apiTestManagement.protocol'), key: 'protocol', ellipsis: {tooltip: true}, width: 80},
  {title: () => t('apiTestManagement.apiType'), key: 'method', ellipsis: {tooltip: true}, width: 100},
  {title: () => t('apiTestManagement.path'), key: 'path', ellipsis: {tooltip: true}, width: 200},
  {
    title: () => t('apiTestManagement.apiStatus'), key: 'status', ellipsis: {tooltip: true}, width: 130,
    render(row) {
      if (hasAnyPermission(['PROJECT_API_DEFINITION:READ+UPDATE'])) {
        return h(NSelect, {value: row.status, options: [], class: 'w-full'}, {});
      } else {
        return h(ApiStatus, {status: row.status})
      }
    }
  },
  {title: () => t('apiTestManagement.belongModule'), key: 'moduleName', ellipsis: {tooltip: true}, width: 200},
  {title: () => t('apiTestManagement.caseTotal'), key: 'caseTotal', ellipsis: {tooltip: true}, width: 100},
  {title: () => t('common.tag'), key: 'tags', ellipsis: {tooltip: true}},
  {title: () => t('apiTestManagement.createTime'), key: 'createTime', ellipsis: {tooltip: true}, width: 180},
  {title: () => t('apiTestManagement.updateTime'), key: 'updateTime', ellipsis: {tooltip: true}, width: 180},
  {
    title: () => hasOperationPermission.value ? t('common.operation') : '', key: 'actions', fixed: 'right',
    width: hasOperationPermission.value ? 200 : 50,
    render(row) {
      return h(NFlex, {}, {
        default: () => [
          withDirectives(h(NButton, {
                type: 'primary',
                text: true,
                onClick: () => handleEditDefinition(row)
              }, {default: () => t('common.edit')}),
              [[permission, ['PROJECT_API_DEFINITION:READ+UPDATE']]]),
          withDirectives(h(NDivider, {vertical: true}, {}), [[permission, ['PROJECT_API_DEFINITION:READ+UPDATE']]]),
          withDirectives(h(NButton, {
                type: 'warning',
                text: true,
                onClick: () => handleExecuteDefinition(row)
              }, {default: () => t('apiTestManagement.execute')}),
              [[permission, ['PROJECT_API_DEFINITION:READ+EXECUTE']]]),
          withDirectives(h(NDivider, {vertical: true}, {}), [[permission, ['PROJECT_API_DEFINITION:READ+EXECUTE']]]),
          withDirectives(h(NButton, {
                type: 'info',
                text: true,
                onClick: () => handleCopyDefinition(row)
              }, {default: () => t('common.copy')}),
              [[permission, ['PROJECT_API_DEFINITION:READ+ADD']]]),
          withDirectives(h(NDivider, {vertical: true}, {}), [[permission, ['PROJECT_API_DEFINITION:READ+ADD']]]),
          withDirectives(h(NButton, {
                type: 'error',
                text: true,
                onClick: () => handleDeleteDefinition(row)
              }, {default: () => t('common.delete')}),
              [[permission, ['PROJECT_API_DEFINITION:READ+DELETE']]]),
        ]
      })
    }
  },
]
const handleEditDefinition = (record: ApiDefinitionDetail) => {
  console.log('editDefinition', record)
}
const handleCopyDefinition = (record: ApiDefinitionDetail) => {
  console.log('copyDefinition', record)
}
const handleExecuteDefinition = (record: ApiDefinitionDetail) => {
  console.log('executeDefinition', record)
}
const handleDeleteDefinition = (record: ApiDefinitionDetail) => {
  console.log('deleteDefinition', record)
}
</script>

<template>
  <div :class="['p-[0_16px_8px_16px]']">
    <w-data-table-tool-bar/>
    <n-data-table :columns="columns" :data="[]">
      <template v-if="hasAnyPermission(['PROJECT_API_DEFINITION:READ+ADD', 'FUNCTIONAL_CASE:READ+IMPORT'])" #empty>
        <n-flex justify="center" class="w-full  p-[8px]">
          {{ t('apiTestManagement.tableNoDataAndPlease') }}
          <n-button type="primary" text class="ml-[8px]" @click="showEditDrawer = true">
            {{ t('apiTestManagement.newApi') }}
          </n-button>
          {{ t('apiTestManagement.or') }}
          <n-button type="primary" text disabled class="ml-[8px]">
            {{ t('common.import') }}
          </n-button>
        </n-flex>
      </template>
    </n-data-table>
    <w-pagination :count="0"/>
  </div>
  <create-or-edit-drawer v-model:active="showEditDrawer"/>
</template>

<style scoped>

</style>