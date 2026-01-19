<script setup lang="ts">
import {type DataTableColumns, type DataTableRowKey, NButton, NInput} from "naive-ui";
import type {IProjectItem, IScheduleInfo} from "/@/api/types.ts";
import {h, onMounted, ref} from "vue";
import WDataTableAction from "/@/components/WDataTableAction.vue";
import {usePagination, useRequest} from "alova/client";
import {scheduleApi} from "/@/api/methods/schedule.ts";
import WDataTableToolBar from "/@/components/WDataTableToolBar.vue";
import WPagination from "/@/components/WPagination.vue";
import EditSchedule from "/@/views/schedule/components/EditSchedule.vue";
import WCronSelect from "/@/components/WCronSelect.vue";

const keyword = ref('')
const showEditModal = ref(false)
const tableMoreAction = [
  {label: '恢复', key: 'resume',},
  {label: '暂停', key: 'pause',},
]
const handleTableMoreAction = (key: string, row: IScheduleInfo) => {
  switch (key) {
    case 'resume':
      window.$message.info(`恢复任务${row.name}`)
      break;
    case 'pause':
      window.$message.info(`暂停任务${row.name}`)
      break;
    case 'delete':
      window.$message.info(`删除任务${row.name}`)
      break;
  }
}
const columns: DataTableColumns<IScheduleInfo> = [
  {type: 'selection', fixed: 'left', options: ['all', 'none']},
  {title: 'ID', key: 'num', ellipsis: {tooltip: true}, width: 220},
  {title: '名称', key: 'name', ellipsis: {tooltip: true}, width: 220},
  {
    title: '运行规则',
    key: 'value', width: 120,
    render: (row) => h(WCronSelect, {
      modelValue: row.value,
      onChangeCron: (v) => handleChangeCron(v, row),
      size: 'small'
    }, {})
  },
  {title: '上次完成时间', key: 'lastTimeAsLocalDateTime', width: 220},
  {title: '下次执行时间', key: 'nextTimeAsLocalDateTime', width: 220},
  {
    title: '操作', key: 'actions', fixed: 'right', width: 200,
    render(row) {
      return h(WDataTableAction, {
        showEdit: true,
        moreAction: tableMoreAction,
        onRePassParameter: (key) => handleTableMoreAction(key, row)
      }, {
        default: () => [
          h(NButton, {text: true}, {default: () => 'once'}),
        ]
      })
    }
  }
]
const checkedRowKeys = ref<DataTableRowKey[]>([])
const handleCheck = (rowKeys: DataTableRowKey[]) => {
  checkedRowKeys.value = rowKeys
}
const {send: updateScheduleCron} = useRequest((param) => scheduleApi.updateScheduleCron(param), {immediate: false})
const handleChangeCron = (value: string, record: IScheduleInfo) => {
  updateScheduleCron({id: record.id, cron: value,}).then(() => {
    window.$message.info(`运行规则修改成功`)
    fetchData()
  })
}
const handleAdd = () => {
  showEditModal.value = true
}
const {page, pageSize, total, data, send: fetchData} = usePagination((page, pageSize) => {
  const params = {page, pageSize, keyword: keyword.value}
  return scheduleApi.querySchedulePage(params)
}, {
  initialData: {total: 0, data: []},
  immediate: false,
  data: resp => resp.records,
  total: resp => resp.totalRow,
  watchingStates: [keyword],
})
onMounted(() => {
  fetchData()
})
</script>

<template>
  <n-card
      title="任务列表"
      :segmented="{content: true,}"
  >
    <template #header-extra>
      <n-input v-model:value="keyword" clearable placeholder="根据任务名称/ID查询"/>
    </template>
    <w-data-table-tool-bar @refresh="fetchData" @add="handleAdd"/>
    <n-data-table :columns="columns"
                  :data="data"
                  :row-key="(row: IProjectItem) => row.id"
                  @update:checked-row-keys="handleCheck"/>
    <w-pagination v-model:page="page" v-model:page-size="pageSize" :count="total||0"/>
    <edit-schedule v-model:show-modal="showEditModal" @cancel="fetchData"/>
  </n-card>
</template>

<style scoped>

</style>