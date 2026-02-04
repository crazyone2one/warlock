<script setup lang="ts">
import {type DataTableColumns, type DataTableRowKey, NButton, NInput, NPopover, NSwitch} from "naive-ui";
import type {IProjectItem, IScheduleInfo} from "/@/api/types.ts";
import {h, onMounted, ref} from "vue";
import WDataTableAction from "/@/components/WDataTableAction.vue";
import {usePagination, useRequest} from "alova/client";
import {scheduleApi} from "/@/api/methods/schedule.ts";
import WDataTableToolBar from "/@/components/WDataTableToolBar.vue";
import WPagination from "/@/components/WPagination.vue";
import EditSchedule from "/@/views/schedule/components/EditSchedule.vue";
import WCronSelect from "/@/components/WCronSelect.vue";
import ScheduleConfigModal from "/@/views/schedule/components/ScheduleConfigModal.vue";
import {useI18n} from "vue-i18n";
import {hasAnyPermission} from "/@/utils/permission.ts";

const {t} = useI18n()
const keyword = ref('')
const currentScheduleId = ref('')
const showEditModal = ref(false)
const showConfigDrawer = ref(false)
const tableMoreAction = [
  {label: t('common.stop'), key: 'pause',},
]
const {send: deleteSchedule} = useRequest(id => scheduleApi.deleteSchedule(id), {immediate: false})
const {send: stopTask} = useRequest(id => scheduleApi.pauseSchedule(id), {immediate: false})
const handleTableMoreAction = (key: string, row: IScheduleInfo) => {
  switch (key) {
    case 'pause':
      window.$dialog.warning({
        title: t('ms.taskCenter.stopTaskTitle'),
        content: t('ms.taskCenter.stopTimeTaskTip'),
        positiveText: t('common.stopConfirm'),
        negativeText: t('common.cancel'),
        onPositiveClick() {
          stopTask(row.id).then(() => {
            window.$message.success(t('common.stopped'))
            fetchData()
          })
        }
      })
      break;
    case 'delete':
      window.$dialog.error({
        title: t('ms.taskCenter.deleteTaskTitle'),
        content: t('ms.taskCenter.deleteCaseTaskTip'),
        positiveText: t('common.confirmDelete'),
        negativeText: t('common.cancel'),
        onPositiveClick() {
          deleteSchedule(row.id).then(() => {
            window.$message.info(t('common.deleteSuccess'))
            fetchData()
          })
        }
      })
      break;
  }
}
const columns: DataTableColumns<IScheduleInfo> = [
  {type: 'selection', fixed: 'left', options: ['all', 'none']},
  {
    title: 'ID', key: 'num', width: 120, render(row) {
      return h(NPopover, {placement: 'right'}, {
        default: () => 'Edit Config',
        trigger: () => h(NButton, {text: true, onClick: () => handleEditConfig(row)}, {default: () => row.num}),
      })
    }
  },
  {title: t('ms.taskCenter.taskName'), key: 'name', ellipsis: {tooltip: true}, width: 200},
  {
    title: t('common.status'), key: 'status', width: 50,
    render(row) {
      return h(NSwitch, {
        value: row.enable, size: 'small',
        disabled: !hasAnyPermission(['SYSTEM_SCHEDULE_TASK_CENTER:READ+UPDATE']),
        loading: switchLoading.value,
        onUpdateValue: (value) => handleSwitch(value, row)
      }, {});
    }
  },
  {
    title: t('ms.taskCenter.runRule'),
    key: 'value', width: 120,
    render(row) {
      if (hasAnyPermission(['SYSTEM_SCHEDULE_TASK_CENTER:READ+UPDATE'])) {
        return h(WCronSelect, {
          modelValue: row.value,
          onChangeCron: (v) => handleChangeCron(v, row),
          size: 'small'
        }, {});
      } else {
        return h('span', {}, {default: () => row.value})
      }
    }
  },
  {title: t('ms.taskCenter.lastFinishTime'), key: 'lastTimeAsLocalDateTime', width: 220},
  {title: t('ms.taskCenter.nextExecuteTime'), key: 'nextTimeAsLocalDateTime', width: 220},
  {
    title: () => t('common.operation'), key: 'actions', fixed: 'right', width: 200,
    render(row) {
      return h(WDataTableAction, {
        showEdit: true,
        moreAction: tableMoreAction,
        onRePassParameter: (key) => handleTableMoreAction(key, row)
      }, {
        default: () => [
          h(NButton, {text: true}, {default: () => t('ms.taskCenter.execute')}),
        ]
      })
    }
  }
]
const handleEditConfig = (row: IScheduleInfo) => {
  showConfigDrawer.value = true
  currentScheduleId.value = row.id
}
const checkedRowKeys = ref<DataTableRowKey[]>([])
const handleCheck = (rowKeys: DataTableRowKey[]) => {
  checkedRowKeys.value = rowKeys
}
const {send: updateScheduleCron} = useRequest((param) => scheduleApi.updateScheduleCron(param), {immediate: false})
const {
  send: switchSchedule,
  loading: switchLoading
} = useRequest((id) => scheduleApi.scheduleSwitch(id), {immediate: false});
const handleSwitch = (_v: boolean, record: IScheduleInfo) => {
  switchSchedule(record.id).then(() => {
    window.$message.success(t(record.enable ? 'ms.taskCenter.closeTaskSuccess' : 'ms.taskCenter.openTaskSuccess'))
    fetchData()
  })
}
const handleChangeCron = (value: string, record: IScheduleInfo) => {
  updateScheduleCron({id: record.id, cron: value,}).then(() => {
    window.$message.success(t('common.updateSuccess'))
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
      <n-input v-model:value="keyword" clearable :placeholder="t('ms.taskCenter.search')"/>
    </template>
    <w-data-table-tool-bar @refresh="fetchData" @add="handleAdd"/>
    <n-data-table :columns="columns"
                  :data="data"
                  :row-key="(row: IProjectItem) => row.id"
                  :checked-row-keys="checkedRowKeys"
                  @update:checked-row-keys="handleCheck"/>
    <w-pagination v-model:page="page" v-model:page-size="pageSize" :count="total||0"/>
    <edit-schedule v-model:show-modal="showEditModal" @cancel="fetchData"/>
    <schedule-config-modal v-model:show-modal="showConfigDrawer"
                           v-model:schedule-id="currentScheduleId"/>
  </n-card>
</template>

<style scoped>

</style>