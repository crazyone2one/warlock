<script setup lang="ts">
import type {CurrentUserGroupItem, UserTableItem} from "/@/api/types/user-group.ts";
import {usePagination} from "alova/client";
import {userGroupApi} from "/@/api/methods/userGroup.ts";
import {hasAnyPermission} from "/@/utils/permission.ts";
import {useI18n} from "vue-i18n";
import {type DataTableColumns, type DataTableRowKey} from "naive-ui";
import {ref} from "vue";
import WPagination from "/@/components/WPagination.vue";

const keyword = defineModel<string>('keyword', {type: String, default: ''});
const props = defineProps<{
  current: CurrentUserGroupItem;
  deletePermission?: string[];
  readPermission?: string[];
  updatePermission?: string[];
}>();
const {t} = useI18n();
const columns: DataTableColumns<UserTableItem> = [
  {type: 'selection', fixed: 'left', options: ['all', 'none']},
  {title: () => t('system.userGroup.name'), key: 'name', ellipsis: {tooltip: true}},
  {title: () => t('system.userGroup.email'), key: 'email', ellipsis: {tooltip: true}},
  {title: () => t('system.userGroup.phone'), key: 'phone', ellipsis: {tooltip: true}},
  {title: () => t('system.userGroup.operation'), key: 'operation', fixed: 'right', width: 120},
]
const {page, pageSize, total, data, send} = usePagination((page, pageSize) => {
  const params = {page, pageSize, keyword: keyword.value, roleId: props.current.id}
  return userGroupApi.queryUserPageByUserGroup(params)
}, {
  initialData: {total: 0, data: []},
  data: resp => resp.records,
  total: resp => resp.totalRow,
  watchingStates: [keyword],
})
const checkedRowKeys = ref<DataTableRowKey[]>([])
const handleCheck = (rowKeys: DataTableRowKey[]) => {
  checkedRowKeys.value = rowKeys
}
const handlePermission = (permission: string[], cb: () => void) => {
  if (!hasAnyPermission(permission)) {
    return false;
  }
  cb();
};
const fetchData = () => {
  handlePermission(props.readPermission || [], async () => {
    await send();
  });
}
defineExpose({
  fetchData,
});
</script>

<template>
  <div>
    <n-data-table :columns="columns"
                  :data="data"
                  :row-key="(row: UserTableItem) => row.id"
                  @update:checked-row-keys="handleCheck"/>
    <w-pagination v-model:page="page" v-model:page-size="pageSize" :count="total || 0"/>
  </div>
</template>

<style scoped>

</style>