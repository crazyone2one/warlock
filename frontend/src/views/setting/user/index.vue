<script setup lang="ts">
import {type DataTableColumns, type DataTableRowKey, NSelect,NSwitch} from "naive-ui";
import WDataTableToolBar from "/@/components/WDataTableToolBar.vue";
import WPagination from "/@/components/WPagination.vue";
import {computed, h, onMounted, ref} from "vue";
import type {SystemRole, UserListItem} from "/@/api/types/user.ts";
import {usePagination} from "alova/client";
import {userApi} from "/@/api/methods/user.ts";
import {useI18n} from "vue-i18n";
import {hasAllPermission, hasAnyPermission} from "/@/utils/permission.ts";
import WTagGroup from "/@/components/WTagGroup.vue";

const {t} = useI18n()
const keyword = ref('')
const userGroupOptions = ref<SystemRole[]>([]);
const checkedRowKeys = ref<DataTableRowKey[]>([])
const handleCheck = (rowKeys: DataTableRowKey[]) => {
  checkedRowKeys.value = rowKeys
}
const hasOperationSysUserPermission = computed(() =>
    hasAnyPermission(['SYSTEM_USER:READ+UPDATE', 'SYSTEM_USER:READ+DELETE'])
);
const columns: DataTableColumns<UserListItem> = [
  {type: 'selection', fixed: 'left', options: ['all', 'none']},
  {title: () => t('system.user.tableColumnName'), key: 'userName', ellipsis: {tooltip: true}},
  {title: () => t('system.user.tableColumnEmail'), key: 'email', ellipsis: {tooltip: true}},
  {title: () => t('system.user.tableColumnPhone'), key: 'phone', ellipsis: {tooltip: true}, width: 140},
  {
    title: () => t('system.user.tableColumnUserGroup'), key: 'userRoleList', width: 300,
    render(row) {
      if (!row.selectUserGroupVisible) {
        return h(WTagGroup, {
          tagList: row.userRoleList, type: "info", allowEdit: true,
          onClick: () => handleTagClick(row)
        }, {});
      } else {
        return h(NSelect, {
          value: row.userRoleIdList,
          options: userGroupOptions.value,
          placeholder: t('system.user.createUserUserGroupPlaceholder'),
          clearable: true, valueField: "id", labelField: "name", multiSelect: true,
          size: 'small', class: "w-full max-w-[200px]",
          loading: row.selectUserGroupLoading,
          disabled: row.selectUserGroupLoading,
          onUpdateShow: (value) => handleUserGroupChange(value, row)
        }, {})
      }
    }
  },
  {
    title: () => t('system.user.tableColumnStatus'), key: 'enable',
    render(row) {
      return h(NSwitch, {value:row.enable,size:'small'}, {})
    }
  },
  {
    title: () => hasOperationSysUserPermission.value ? t('system.user.tableColumnActions') : '',
    key: 'actions',
    fixed: 'right',
    width: 200,
  }
]
const handleTagClick = (record: UserListItem) => {
  if (hasAllPermission(['SYSTEM_USER:READ+UPDATE', 'SYSTEM_USER_ROLE:READ'])) {
    record.selectUserGroupVisible = true;
  }
}
const handleUserGroupChange = (val: boolean, record: UserListItem & Record<string, any>) => {
  if (!val) {
    record.selectUserGroupLoading = true;
    const params = {
      id: record.id,
      name: record.userName,
      email: record.email,
      phone: record.phone,
      userRoleIdList: record.userRoleIdList,
    };
    console.log(params)
    record.selectUserGroupVisible = false;
  }
}
const {page, pageSize, total, data, send: fetchData} = usePagination((page, pageSize) => {
  const params = {page, pageSize, keyword: keyword.value}
  return userApi.queryUserPage(params)
}, {
  initialData: {total: 0, data: []},
  immediate: false,
  data: resp => resp.records,
  total: resp => resp.totalRow,
  watchingStates: [keyword],
})
const handleAddUser = () => {

}
onMounted(() => {
  fetchData()
})
</script>

<template>
  <n-card
      title="用户列表"
      :segmented="{content: true,}"
  >
    <template #header-extra>
      <n-input v-model:value="keyword" clearable :placeholder="t('system.user.searchUser')"/>
    </template>
    <w-data-table-tool-bar :add-permission="['SYSTEM_USER:READ+ADD']"
                           :add-icon="`i-solar:user-plus-linear`"
                           @refresh="fetchData"
                           @add="handleAddUser">
      <template #left>
        <n-button v-permission.all="['SYSTEM_USER:READ+IMPORT']" type="primary" text class="mr-3">
          <template #icon>
            <n-icon>
              <div class="i-solar:cloud-upload-linear"/>
            </n-icon>
          </template>
          {{ t('system.user.importUser') }}
        </n-button>
      </template>
    </w-data-table-tool-bar>
    <n-data-table :columns="columns"
                  :data="data"
                  :row-key="(row: UserListItem) => row.id"
                  @update:checked-row-keys="handleCheck"/>
    <w-pagination v-model:page="page" v-model:page-size="pageSize" :count="total||0"/>
  </n-card>
</template>

<style scoped>

</style>