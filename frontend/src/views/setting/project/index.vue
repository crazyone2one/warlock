<script setup lang="ts">
import {h, onMounted, ref} from 'vue'
import type {IProjectItem} from "/@/api/types.ts";
import {type DataTableColumns, type DataTableRowKey, NFlex, NInput} from "naive-ui";
import {usePagination, useRequest} from "alova/client";
import {projectApi} from "/@/api/methods/project.ts";
import WPagination from "/@/components/WPagination.vue";
import WDataTableToolBar from "/@/components/WDataTableToolBar.vue";
import WDataTableAction from "/@/components/WDataTableAction.vue";
import EditProjectModal from "/@/views/setting/project/EditProjectModal.vue";

const keyword = ref('')
const confirmName = ref('')
const showEditProjectModal = ref(false)
const currentProject = ref<IProjectItem | null>(null)
const columns: DataTableColumns<IProjectItem> = [
  {type: 'selection', fixed: 'left', options: ['all', 'none']},
  {title: 'ID', key: 'num', ellipsis: {tooltip: true}},
  {title: '名称', key: 'name', ellipsis: {tooltip: true}},
  {title: '描述', key: 'description', ellipsis: {tooltip: true}},
  {
    title: '操作', key: 'actions', fixed: 'right', width: 200,
    render(row) {
      return h(WDataTableAction, {
        onEditProject: () => handleEditProject(row),
        onDeleteProject: () => handleDeleteProject(row)
      }, {
        default: () => h('div', null, {default: () => 'xx'})
      })
    }
  }
]
const checkedRowKeys = ref<DataTableRowKey[]>([])
const handleCheck = (rowKeys: DataTableRowKey[]) => {
  checkedRowKeys.value = rowKeys
}

const {page, pageSize, total, data, send: fetchData} = usePagination((page, pageSize) => {
  const params = {page, pageSize, keyword: keyword.value}
  return projectApi.queryProjectPage(params)
}, {
  initialData: {total: 0, data: []},
  immediate: false,
  data: resp => resp.records,
  total: resp => resp.totalRow,
  watchingStates: [keyword],
})
const {loading, send: deleteProject} = useRequest(id => projectApi.deleteProject(id), {immediate: false})
const handleAddProject = () => {
  showEditProjectModal.value = true
}
const handleEditProject = (record: IProjectItem) => {
  showEditProjectModal.value = true
  currentProject.value = record
}
const handleDeleteProject = (record: IProjectItem) => {
  currentProject.value = record
  window.$dialog.error({
    title: '删除项目',
    content: () => h(NFlex, {vertical: true}, {
      default: () => {
        return [
          h('div', {class: 'text-red-500'}, {default: () => '删除后，相关的任务将一并被删除'}),
          h('div', {class: 'flex'}, {
            default: () => {
              return [
                h('div', {}, {default: () => '确定删除项目，输入项目名称'}),
                h('div', {class: 'text-red-500'}, {default: () => `${currentProject.value?.name}`}),
                h('div', {}, {default: () => '确认删除'})
              ]
            }
          }),
          // h('div', {}, {default: () => `确定删除项目，输入项目名称${currentProject.value?.name}确认删除`}),
          h(NInput, {
            size: 'small',
            placeholder: '请输入项目名称',
            value: confirmName.value,
            onUpdateValue: (value: string) => confirmName.value = value
          }, {}),
        ]
      }
    }),
    positiveText: '删除',
    negativeText: '取消',
    loading: loading.value,
    onPositiveClick() {
      if (confirmName.value.trim() === currentProject.value?.name) {
        deleteProject(currentProject.value?.id).then(() => {
          window.$message.success('删除成功')
          fetchData()
        })
      } else {
        window.$message.warning('项目名称错误')
      }
    }
  })
}
const handleCancel = (shouldSearch: boolean) => {
  showEditProjectModal.value = false
  if (shouldSearch) {
    fetchData()
  }
}
onMounted(() => {
  fetchData()
})
</script>

<template>
  <n-card
      title="项目列表"
      :segmented="{content: true,}"
  >
    <template #header-extra>
      <n-input v-model:value="keyword" clearable placeholder="根据项目名称/num查询"/>
    </template>
    <w-data-table-tool-bar @refresh="fetchData" @add="handleAddProject"/>
    <n-data-table :columns="columns"
                  :data="data"
                  :row-key="(row: IProjectItem) => row.id"
                  @update:checked-row-keys="handleCheck"/>
    <w-pagination v-model:page="page" v-model:page-size="pageSize" :count="total||0"/>
    <edit-project-modal v-model:show-modal="showEditProjectModal" v-model:current-project="currentProject"
                        @cancel="handleCancel"/>
  </n-card>
</template>

<style scoped>

</style>