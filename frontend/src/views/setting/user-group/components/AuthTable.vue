<script setup lang="ts">
import {type DataTableColumns, NCheckbox, NCheckboxGroup, NFlex} from "naive-ui";
import type {
  AuthTableItem,
  CurrentUserGroupItem,
  SavePermissions,
  UserGroupAuthSetting,
  UserGroupPermissionItem
} from "/@/api/types/user-group.ts";
import {useI18n} from "vue-i18n";
import {computed, h, ref, watchEffect} from "vue";
import {useRequest} from "alova/client";
import {userGroupApi} from "/@/api/methods/userGroup.ts";

const {current = {id: '', name: '', code: ''}, showBottom = true, disabled = false} = defineProps<{
  current: CurrentUserGroupItem;
  savePermission?: string[];
  showBottom?: boolean;
  disabled?: boolean;
}>();
const {t} = useI18n();
// const systemType = inject<AuthScopeEnumType>('systemType');
// 表格的总全选
const allChecked = ref(false);
const allIndeterminate = ref(false);
// 是否可以保存
const canSave = ref(false);
const systemAdminDisabled = computed(() => {
  const adminArr = ['admin', 'org_admin', 'project_admin'];
  const {code} = current;
  if (adminArr.includes(code)) {
    // 系统管理员,组织管理员，项目管理员都不可编辑
    return true;
  }

  return disabled;
});
const columns: DataTableColumns<AuthTableItem> = [
  {title: () => t('system.userGroup.function'), key: 'ability', width: 80},
  {title: () => t('system.userGroup.operationObject'), key: 'operationObject', width: 170},
  {
    title: () => {
      return h(NFlex, {justify: 'space-between'}, {
        default: () => {
          return [
            h('span', null, t('system.userGroup.auth')),
            h(NCheckbox, {
              checked: allChecked.value, indeterminate: allIndeterminate.value,
              disabled: systemAdminDisabled.value || disabled
            }, {})
          ]
        }
      })
    },
    key: 'auth',
    render(row, index) {
      return h(NFlex, {justify: 'space-between'}, {
        default: () => {
          return [
            h(NCheckboxGroup, {
              value: row.perChecked,
              onUpdateValue: (v, e) => handleCellAuthChange(v, index, row, e)
            }, {
              default: () => {
                return row.permissions?.map((item) => {
                  return h(NCheckbox, {value: item.id, label: item.name},);
                });
              }
            }),
            h(NCheckbox, {
              checked: row.enable, indeterminate: row.indeterminate,
              disabled: systemAdminDisabled.value || disabled,
              onUpdateChecked: (value) => handleRowAuthChange(value, index)
            }, {})
          ]
        }
      })
    }
  },
]
const handleCellAuthChange = (_value: (string | number)[], rowIndex: number,
                              row: AuthTableItem,
                              e: {
                                actionType: 'check' | 'uncheck';
                                value: string | number;
                              }) => {
  setAutoRead(row, e.value as string);
  if (!tableData.value) return;
  const tmpArr = tableData.value;
  const tmpRow = tmpArr[rowIndex];
  // 检查row是否存在以及permissions是否存在
  if (!tmpRow) return;
  const length = tmpRow.permissions?.length || 0;
  if (row.perChecked?.length === length) {
    tmpRow.enable = true;
    tmpRow.indeterminate = false;
  } else if (row.perChecked?.length === 0) {
    tmpRow.enable = false;
    tmpRow.indeterminate = false;
  } else {
    tmpRow.enable = false;
    tmpRow.indeterminate = true;
  }
  handleAllChange();
}
const setAutoRead = (record: AuthTableItem, currentValue: string) => {
  if (!record.perChecked?.includes(currentValue)) {
    // 如果当前没有选中则执行自动添加查询权限逻辑
    // 添加权限值
    record.perChecked?.push(currentValue);
    const preStr = currentValue.split(':')[0];
    const postStr = currentValue.split(':')[1];
    const lastEditStr = currentValue.split('+')[1]; // 编辑类权限通过+号拼接
    const existRead = record.perChecked?.some(
        (item: string) => item.split(':')[0] === preStr && item.split(':')[1] === 'READ'
    );
    const existCreate = record.perChecked?.some(
        (item: string) => item.split(':')[0] === preStr && item.split(':')[1] === 'ADD'
    );
    if (!existRead && postStr !== 'READ') {
      record.perChecked?.push(`${preStr}:READ`);
    }
    if (!existCreate && lastEditStr === 'IMPORT') {
      // 勾选导入时自动勾选新增和查询
      record.perChecked?.push(`${preStr}:ADD`);
      record.perChecked?.push(`${preStr}:READ+UPDATE`);
    }
  } else {
    // 删除权限值
    const preStr = currentValue.split(':')[0];
    const postStr = currentValue.split(':')[1];
    if (postStr === 'READ') {
      // 当前是查询 那 移除所有相关的
      record.perChecked = record.perChecked.filter((item: string) => !item.includes(preStr as string));
    } else {
      record.perChecked.splice(record.perChecked.indexOf(currentValue), 1);
    }
  }
}
const handleRowAuthChange = (value: boolean, rowIndex: number) => {
  if (!tableData.value) return;
  const tmpArr = tableData.value;
  const tmpRow = tmpArr[rowIndex];
  if (!tmpRow) return;
  tmpRow.indeterminate = false;
  if (value) {
    tmpRow.enable = true;
    tmpRow.perChecked = tmpRow.permissions?.map((item: UserGroupPermissionItem) => item.id);
  } else {
    tmpRow.enable = false;
    tmpRow.perChecked = [];
  }
  tableData.value = [...tmpArr];
  handleAllChange();
  if (!canSave.value) canSave.value = true;
}
const makeData = (item: UserGroupAuthSetting) => {
  const result: AuthTableItem[] = [];
  item.children?.forEach((child, index) => {
    const perChecked =
        child?.permissions?.reduce((acc: string[], cur) => {
          if (cur.enable) {
            acc.push(cur.id);
          }
          return acc;
        }, []) || [];
    const perCheckedLength = perChecked.length;
    let indeterminate = false;
    if (child?.permissions) {
      indeterminate = perCheckedLength > 0 && perCheckedLength < child?.permissions?.length;
    }
    result.push({
      id: child?.id,
      enable: child?.enable,
      permissions: child?.permissions,
      indeterminate,
      perChecked,
      ability: index === 0 ? item.name : undefined,
      operationObject: child.name,
      rowSpan: index === 0 ? item.children?.length || 1 : undefined,
    });
  });
  return result;
}
const transformData = (data: UserGroupAuthSetting[]) => {
  const result: AuthTableItem[] = [];
  data.forEach((item) => {
    result.push(...makeData(item));
  });
  return result;
}
const handleAllChange = (isInit = false) => {
  if (!tableData.value) {
    return
  }
  const tmpArr = tableData.value;
  const {length: allLength} = tmpArr;
  const {length} = tmpArr.filter((item: UserGroupAuthSetting) => item.enable);
  if (length === allLength) {
    allChecked.value = true;
    allIndeterminate.value = false;
  } else if (length === 0) {
    allChecked.value = false;
    allIndeterminate.value = false;
  } else {
    allChecked.value = false;
    allIndeterminate.value = true;
  }
  if (!isInit && !canSave.value) canSave.value = true;
}
const {
  send: fetchData,
  data: tableData
} = useRequest(id => userGroupApi.getGlobalUSetting(id, transformData), {immediate: false})
const initData = (id: string) => {
  fetchData(id).then(() => {
    handleAllChange(true);
  })
}
// 恢复默认值
const handleReset = () => {
  if (current.id) {
    initData(current.id);
  }
};
const {send: saveData} = useRequest(p => userGroupApi.saveGlobalUSetting(p), {immediate: false})
const handleSave = () => {
  if (!tableData.value) return;
  const permissions: SavePermissions[] = [];
  const tmpArr = tableData.value;
  tmpArr.forEach((item: AuthTableItem) => {
    item.permissions?.forEach((ele) => {
      ele.enable = item.perChecked?.includes(ele.id) || false;
      permissions.push({id: ele.id, enable: ele.enable});
    });
  });
  saveData({
    userRoleId: current.id,
    permissions,
  }).then(() => {
    canSave.value = false;
    window.$message.success(t('common.saveSuccess'));
    initData(current.id);
  })
}
watchEffect(() => {
  if (current.id) {
    initData(current.id);
  }
});
defineExpose({
  canSave,
  handleSave,
  handleReset,
});
</script>

<template>
  <div class="flex h-full flex-col gap-[16px] overflow-hidden">
    <div class="group-auth-table">
      <n-data-table :columns="columns" :data="tableData"/>
    </div>
    <div
        v-if="showBottom && current.code !== 'admin' && !systemAdminDisabled"
        v-permission="savePermission || []"
        class="footer"
    >
      <n-button :disabled="!canSave" @click="handleReset">{{ t('system.userGroup.reset') }}</n-button>
      <n-button v-permission="savePermission || []" :disabled="!canSave" type="primary"
                @click="handleSave">
        {{ t('system.userGroup.save') }}
      </n-button>
    </div>
  </div>
</template>

<style scoped>
.group-auth-table {
  @apply flex-1 overflow-hidden;
  padding: 0 16px 16px;
}

.footer {
  display: flex;
  justify-content: flex-end;
  padding: 24px;
  box-shadow: 0 -1px 4px rgb(2 2 2 / 10%);
  gap: 16px;
}
</style>