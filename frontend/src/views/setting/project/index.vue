<script setup lang="ts">
import {computed, h, onMounted, ref, withDirectives} from 'vue'
import type {IProjectItem} from "/@/api/types.ts";
import {type DataTableColumns, type DataTableRowKey, NButton, NFlex, NInput, NPopover, NSwitch} from "naive-ui";
import {usePagination, useRequest} from "alova/client";
import {projectApi} from "/@/api/methods/project.ts";
import WPagination from "/@/components/WPagination.vue";
import WDataTableToolBar from "/@/components/WDataTableToolBar.vue";
import EditProjectModal from "/@/views/setting/project/EditProjectModal.vue";
import ProjectConfigModal from "/@/views/setting/project/ProjectConfigModal.vue";
import {useI18n} from "vue-i18n";
import WShowOrEdit from "/@/components/w-table/WShowOrEdit.vue";
import {hasAnyPermission} from "/@/utils/permission.ts";
import permission from "/@/directive/permission";

const {t} = useI18n()
const keyword = ref('')
const confirmName = ref('')
const showEditProjectModal = ref(false)
const showConfigModal = ref(false)
const currentProject = ref<IProjectItem>()
const hasOperationPermission = computed(() =>
    hasAnyPermission([
      'SYSTEM_ORGANIZATION_PROJECT:READ+UPDATE',
      'SYSTEM_ORGANIZATION_PROJECT:READ+DELETE',
    ])
);
const operationWidth = computed(() => {
  if (hasOperationPermission.value) {
    return 250;
  }
  if (hasAnyPermission(['PROJECT_BASE_INFO:READ'])) {
    return 100;
  }
  return 50;
});
const columns: DataTableColumns<IProjectItem> = [
  {type: 'selection', fixed: 'left', options: ['all', 'none']},
  {
    title: 'ID', key: 'num', render(row) {
      return h(NPopover, {placement: 'right'}, {
        default: () => 'Edit Config',
        trigger: () => h(NButton, {text: true, onClick: () => handleEditConfig(row)}, {default: () => row.num}),
      })
    }
  },
  {
    title: t('system.organization.name'), key: 'name', ellipsis: {tooltip: true},
    render(row) {
      if (hasAnyPermission(['SYSTEM_PROJECT:READ+UPDATE'])) {
        return h(WShowOrEdit, {inputValue: row.name, onConfirm: (value: string) => handleEditProjectName(value, row)})
      }
      return h('div', {}, {default: () => row.name})
    }
  },
  {title: t('common.desc'), key: 'description', ellipsis: {tooltip: true}},
  {
    title: t('system.organization.status'), key: 'enable', render(row) {
      return h(NSwitch, {
        value: row.enable,
        size: 'small',
        disabled: !hasAnyPermission(['SYSTEM_PROJECT:READ+UPDATE']),
        onUpdateValue: (value: boolean) => handleChangeProjectStatus(value, row)
      })
    }
  },
  {
    title: hasOperationPermission.value ? t('system.organization.operation') : '',
    key: 'actions',
    fixed: 'right',
    width: operationWidth.value,
    render(row) {
      const res = []
      const deleteProject = h(NButton, {
        text: true,
        size: 'small', type: 'error',
        onClick: () => handleDeleteProject(row),
      }, {default: () => t('common.delete')});
      const edit = h(NButton, {
        text: true,
        size: 'small', type: 'info',
        onClick: () => handleEditProject(row),
      }, {default: () => t('common.edit')});
      const addMember = h(NButton, {
        text: true,
        size: 'small',
        disabled: true,
      }, {default: () => t('system.organization.addMember')});
      if (!row.enable) {
        res.push(withDirectives(deleteProject, [[permission, ['SYSTEM_PROJECT:READ+DELETE']]]))
        return res;
      } else {
        res.push(h(NFlex, {}, {
          default: () => {
            return [
              withDirectives(edit, [[permission, ['SYSTEM_PROJECT:READ+UPDATE']]]),
              withDirectives(addMember, [[permission, ['SYSTEM_PROJECT:READ+UPDATE']]]),
              withDirectives(deleteProject, [[permission, ['SYSTEM_PROJECT:READ+DELETE']]]),
            ]
          }
        }))
        return res
      }
    }
  }
]
const handleEditConfig = (row: IProjectItem) => {
  showConfigModal.value = true
  currentProject.value = row
}
const {send: handleRename} = useRequest((data) => projectApi.renameProject(data), {immediate: false})
const handleEditProjectName = (value: string, record: IProjectItem) => {
  handleRename({id: record.id, name: value}).then(() => {
    window.$message.success(t('common.updateSuccess'))
    fetchData()
  })
}
const checkedRowKeys = ref<DataTableRowKey[]>([])
const handleCheck = (rowKeys: DataTableRowKey[]) => {
  checkedRowKeys.value = rowKeys
}
const {send: enableOrDisableProject} = useRequest((id, enable) => projectApi.enableOrDisableProject(id, enable), {immediate: false})
const handleChangeProjectStatus = (isEnable: boolean, record: IProjectItem) => {
  window.$dialog.warning({
    title: isEnable ? t('system.project.enableTitle') : t('system.project.endTitle'),
    autoFocus: false,
    content: isEnable ? t('system.project.enableContent') : t('system.project.endContent'),
    positiveText: isEnable ? t('common.confirmStart') : t('common.confirmClose'),
    negativeText: t('common.cancel'),
    onPositiveClick() {
      enableOrDisableProject(record.id, isEnable).then(() => {
        fetchData()
        window.$message.success(isEnable ? t('common.enableSuccess') : t('common.closeSuccess'))
      })
    }
  })
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
    <w-data-table-tool-bar :add-permission="['SYSTEM_PROJECT:READ+CREATE']" @refresh="fetchData"
                           @add="handleAddProject"/>
    <n-data-table :columns="columns"
                  :data="data"
                  :row-key="(row: IProjectItem) => row.id"
                  v-model:checked-row-keys="checkedRowKeys"
                  @update:checked-row-keys="handleCheck"/>

    <n-flex justify="space-between">
      <div class="mt-5">
        <slot name="count">
          {{ t('msTable.batch.selected', {count: checkedRowKeys.length}) }}
        </slot>
        <n-button class="clear-btn ml-[12px] px-2" text @click="checkedRowKeys = []">
          {{ t('msTable.batch.clear') }}
        </n-button>
      </div>
      <w-pagination v-model:page="page" v-model:page-size="pageSize" :count="total||0"/>
    </n-flex>
    <edit-project-modal v-model:show-modal="showEditProjectModal" v-model:current-project="currentProject"
                        @cancel="handleCancel"/>
    <project-config-modal v-model:show-modal="showConfigModal" v-model:current-project="currentProject"/>
  </n-card>
</template>

<style scoped>

</style>