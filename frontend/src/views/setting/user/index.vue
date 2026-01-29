<script setup lang="ts">
import {
  type DataTableColumns,
  type DataTableRowKey,
  type DropdownOption,
  type FormInst,
  NAlert,
  NButton,
  NFlex,
  NSelect,
  NSwitch
} from "naive-ui";
import WDataTableToolBar from "/@/components/WDataTableToolBar.vue";
import WPagination from "/@/components/WPagination.vue";
import {computed, h, onMounted, type Ref, ref} from "vue";
import type {SystemRole, UserForm, UserListItem, UserModalMode} from "/@/api/types/user.ts";
import {usePagination, useRequest} from "alova/client";
import {userApi} from "/@/api/methods/user.ts";
import {useI18n} from "vue-i18n";
import {hasAllPermission, hasAnyPermission} from "/@/utils/permission.ts";
import WTagGroup from "/@/components/WTagGroup.vue";
import WTableMoreAction from "/@/components/WTableMoreAction.vue";
import WBatchForm from '/@/components/w-batch-form/index.vue'
import {validateEmail, validatePhone} from "/@/utils/validate.ts";
import type {FormItemModel} from "/@/components/w-batch-form/types.ts";
import type {BatchActionQueryParams} from "/@/api/types.ts";
import {characterLimit} from "/@/utils";

const batchFormRef = ref<InstanceType<typeof WBatchForm>>();
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
const tableActions = [
  {
    label: () => t('system.user.resetPassword'),
    key: 'resetPassword',
    permission: ['SYSTEM_USER:READ+UPDATE'],
  },
  {
    type: 'divider',
    key: 'd1'
  },
  {
    label: () => t('system.user.delete'),
    key: 'delete',
    permission: ['SYSTEM_USER:READ+DELETE'],
  },
]
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
          clearable: true, valueField: "id", labelField: "name", multiple: true, filterable: true,
          size: 'small', class: "w-full max-w-[200px]",
          loading: row.selectUserGroupLoading,
          disabled: row.selectUserGroupLoading,
          onUpdateShow: (value) => handleUserGroupChange(value, row),
          onUpdateValue: (value) => row.userRoleIdList = value,
        }, {})
      }
    }
  },
  {
    title: () => t('system.user.tableColumnStatus'), key: 'enable',
    render(row) {
      return h(NSwitch, {
        value: row.enable, size: 'small',
        disabled: !hasAnyPermission(['SYSTEM_USER:READ+UPDATE']),
        onUpdateValue: (value) => handleEnableChange(value, row)
      }, {})
    }
  },
  {
    title: () => hasOperationSysUserPermission.value ? t('system.user.tableColumnActions') : '',
    key: 'actions', fixed: 'right', width: 200,
    render(row) {
      if (!row.enable) {
        return h(NButton, {
              text: true,
              directives: [{name: 'permission', value: ['SYSTEM_USER:READ+DELETE']}],
              type: 'error',
            },
            {default: () => t('system.user.delete')});
      } else {
        return h(NFlex, {}, {
          default: () => {
            const res = []
            if (hasAnyPermission(['SYSTEM_USER:READ+UPDATE'])) {
              res.push(h(NButton, {
                    text: true,
                    // directives: [{name: 'permission', value: ['SYSTEM_USER:READ+UPDATE']}],
                    type: 'primary', onClick: () => showUserModal('edit', row),
                  },
                  {default: () => t('system.user.editUser')}),)
            }
            if (hasAnyPermission(['SYSTEM_USER:READ+UPDATE', 'SYSTEM_USER:READ+DELETE'])) {
              res.push(
                  h(WTableMoreAction, {
                        // directives: [{name: 'permission', value: ['SYSTEM_USER:READ+UPDATE', 'SYSTEM_USER:READ+DELETE']}],
                        options: tableActions,
                        onSelect: (key) => handleMoreAction(key, row),
                      },
                  )
              )
            }
            return res;
          }
        });
      }
    }
  }
]
const handleMoreAction = (option: DropdownOption, record: UserListItem) => {
  switch (option.key) {
    case 'resetPassword':
      resetPassword(record);
      break
    case 'delete':
      deleteUser(record);
      break
  }
}
const deleteUser = (record?: UserListItem, isBatch?: boolean, params?: BatchActionQueryParams) => {
  let title = t('system.user.deleteUserTip', {name: characterLimit(record?.userName)});
  let selectIds = [record?.id || ''];
  if (isBatch) {
    title = t('system.user.batchDeleteUserTip', {count: params?.currentSelectCount || checkedRowKeys.value.length});
    selectIds = checkedRowKeys.value as string[];
  }
  let content = t('system.user.deleteUserContent');
  window.$dialog.warning({
    title,
    content,
    positiveText: t('system.user.deleteUserConfirm'),
    negativeText: t('system.user.deleteUserCancel'),
    maskClosable: false,
    onPositiveClick: async () => {
      await userApi.deleteUserInfo({
        selectIds,
        selectAll: !!params?.selectAll,
        excludeIds: params?.excludeIds || [],
        condition: {keyword: keyword.value},
      })
      window.$message.success(t('system.user.deleteUserSuccess'));
      await fetchData();
    },
  })
}
const resetPassword = (record?: UserListItem, isBatch?: boolean, params?: BatchActionQueryParams) => {
  let title = t('system.user.resetPswTip', {name: characterLimit(record?.userName)});
  let selectIds = [record?.id || ''];
  if (isBatch) {
    title = t('system.user.batchResetPswTip', {count: params?.currentSelectCount || checkedRowKeys.value.length});
    selectIds = checkedRowKeys.value as string[];
  }

  let content = t('system.user.resetPswContent');
  if (record && record.id === 'admin') {
    content = t('system.user.resetAdminPswContent');
  }
  window.$dialog.warning({
    title,
    content,
    positiveText: t('system.user.resetPswConfirm'),
    negativeText: t('system.user.resetPswCancel'),
    maskClosable: false,
    onPositiveClick: async () => {
      await userApi.resetUserPassword({
        selectIds,
        selectAll: !!params?.selectAll,
        excludeIds: params?.excludeIds || [],
        condition: {keyword: keyword.value},
      })
      window.$message.success(t('system.user.resetPswSuccess'));
    },
  })
}
const handleEnableChange = (v: boolean, record: UserListItem) => {
  if (v) {
    enableUser(record);
  } else {
    disabledUser(record);
  }
}
const enableUser = (record?: UserListItem, isBatch?: boolean, params?: BatchActionQueryParams) => {
  let title = t('system.user.enableUserTip', {name: characterLimit(record?.userName)});
  let selectIds = [record?.id || ''];
  if (isBatch) {
    title = t('system.user.batchEnableUserTip', {count: params?.currentSelectCount || checkedRowKeys.value.length});
    selectIds = checkedRowKeys.value as string[];
  }
  window.$dialog.info({
    title,
    content: () => t('system.user.enableUserContent'),
    positiveText: t('system.user.enableUserConfirm'),
    negativeText: t('system.user.enableUserCancel'),
    maskClosable: false,
    onPositiveClick: async () => {
      await userApi.toggleUserStatus({
        selectIds,
        selectAll: !!params?.selectAll,
        excludeIds: params?.excludeIds || [],
        condition: {keyword: keyword.value},
        enable: true,
      })
      window.$message.success(t('system.user.enableUserSuccess'));
      await fetchData();
    },
  })
}
const disabledUser = (record?: UserListItem, isBatch?: boolean, params?: BatchActionQueryParams) => {
  let title = t('system.user.disableUserTip', {name: characterLimit(record?.userName)});
  let selectIds = [record?.id || ''];
  if (isBatch) {
    title = t('system.user.batchDisableUserTip', {count: params?.currentSelectCount || checkedRowKeys.value.length});
    selectIds = checkedRowKeys.value as string[];
  }
  window.$dialog.warning({
    title,
    content: () => t('system.user.disableUserContent'),
    positiveText: t('system.user.disableUserConfirm'),
    negativeText: t('system.user.disableUserCancel'),
    maskClosable: false,
    onPositiveClick: async () => {
      await userApi.toggleUserStatus({
        selectIds,
        selectAll: !!params?.selectAll,
        excludeIds: params?.excludeIds || [],
        condition: {keyword: keyword.value},
        enable: false,
      })
      window.$message.success(t('system.user.disableUserSuccess'));
      await fetchData();
    },
  })
}
const handleTagClick = (record: UserListItem) => {
  if (hasAllPermission(['SYSTEM_USER:READ+UPDATE', 'SYSTEM_USER_ROLE:READ'])) {
    record.selectUserGroupVisible = true;
  }
}
const handleUserGroupChange = async (val: boolean, record: UserListItem & Record<string, any>) => {
  if (!val) {
    record.selectUserGroupLoading = true;
    const params = {
      id: record.id,
      name: record.userName,
      nickName: record.nickName,
      email: record.email,
      phone: record.phone,
      userRoleIdList: record.userRoleIdList,
    };
    await userApi.updateUserInfo(params)
    window.$message.success(t('system.user.updateUserSuccess'));
    record.selectUserGroupVisible = false;
  }
}
const {page, pageSize, total, data, send: fetchData} = usePagination((page, pageSize) => {
  const params = {page, pageSize, keyword: keyword.value}
  return userApi.queryUserPage(params)
}, {
  initialData: {total: 0, data: []},
  data: resp => resp.records,
  total: resp => resp.totalRow,
  watchingStates: [keyword],
})
const {send: fetchSystemRoles} = useRequest(() => userApi.getSystemRoles(), {immediate: false});
const init = () => {
  fetchSystemRoles().then(resp => {
    userGroupOptions.value = resp
    if (userGroupOptions.value.length) {
      userForm.value.userGroup = userGroupOptions.value.filter((e: SystemRole) => e.disabled);
    }
  })
}
const visible = ref(false);
const userFormMode = ref<UserModalMode>('create');
const defaultUserForm = {
  list: [
    {
      name: '',
      nickName: '',
      email: '',
      phone: '',
    },
  ],
  userGroup: [],
  userGroupIdList: [],
};
const userForm = ref<UserForm>(defaultUserForm);
const batchFormModels: Ref<FormItemModel[]> = ref([
  {
    field: 'name',
    type: 'input',
    label: 'system.user.createUserName',
    rules: [{required: true, message: t('system.user.createUserNameNotNull')}, {
      validator(_rule: any, value: string) {
        if (!value) {
          return new Error(t('system.user.createUserNameNotNull'))
        } else if (value.length > 255) {
          return new Error(t('system.user.createUserNameOverLength'))
        }
      }
    }],
    placeholder: 'system.user.createUserNamePlaceholder',
  },
  {
    field: 'nickName',
    type: 'input',
    label: 'system.user.createNickUserName',
    rules: [{required: true, message: t('system.user.createNickUserNameNotNull')}, {
      validator(_rule: any, value: string) {
        if (!value) {
          return new Error(t('system.user.createNickUserNameNotNull'))
        } else if (value.length > 255) {
          return new Error(t('system.user.createNickUserNameOverLength'))
        }
      }
    }],
    placeholder: 'system.user.createNickUserNamePlaceholder',
  },
  {
    field: 'email',
    type: 'input',
    label: 'system.user.createUserEmail',
    rules: [
      {required: true, message: t('system.user.createUserEmailNotNull')},
      {
        validator(_rule: any, value: string) {
          if (!value) {
            return new Error(t('system.user.createUserEmailNotNull'))
          } else if (!validateEmail(value)) {
            return new Error(t('system.user.createUserEmailErr'))
          }
        }
      },
      {notRepeat: true, message: 'system.user.createUserEmailNoRepeat'},
    ],
    placeholder: 'system.user.createUserEmailPlaceholder',
  },
  {
    field: 'phone',
    type: 'input',
    label: 'system.user.createUserPhone',
    rules: [{
      validator(_rule: any, value: string) {
        if (value !== null && value !== '' && value !== undefined && !validatePhone(value)) {
          return new Error(t('system.user.createUserPhoneErr'))
        }
      }
    },],
    placeholder: 'system.user.createUserPhonePlaceholder',
  },
]);
const isBatchFormChange = ref(false);
const handleBatchFormChange = () => {
  isBatchFormChange.value = true;
}
const showUserModal = (mode: UserModalMode, record?: UserListItem) => {
  visible.value = true;
  userFormMode.value = mode;
  if (mode === 'edit' && record) {
    userForm.value.list = [
      {
        id: record.id,
        name: record.userName,
        nickName: record.nickName,
        email: record.email,
        phone: record.phone ? record.phone.replace(/\s/g, '') : record.phone,
      },
    ];
    userForm.value.userGroup = record.userRoleList;
    userForm.value.userGroupIdList = record.userRoleList.map(e => e.id);
  }
}
const userFormRef = ref<FormInst | null>(null);
const resetUserForm = () => {
  userForm.value.list = [];
  userForm.value.userGroupIdList = [];
  userFormRef.value?.restoreValidation();
  userForm.value.userGroup = userGroupOptions.value.filter((e: SystemRole) => e.disabled);
}
const cancelCreate = () => {
  visible.value = false;
  resetUserForm();
}
const handleBeforeClose = () => {
  if (isBatchFormChange.value) {
    window.$dialog.warning({
      title: () => t('common.tip'),
      content: () => t('system.user.closeTip'),
      positiveText: t('common.close'),
      onPositiveClick: () => {
        cancelCreate()
      }
    })
  } else {
    cancelCreate()
  }
}
const loading = ref(false);
const userFormValidate = (cb: () => Promise<any>) => {
  userFormRef.value?.validate(errors => {
    if (errors) {
      return;
    }
    batchFormRef.value?.formValidate(async (list: any) => {
      try {
        loading.value = true;
        userForm.value.list = [...list];
        await cb();
      } finally {
        loading.value = false;
      }
    })
  })
}
const createUser = async (isContinue?: boolean) => {
  const params = {
    userInfoList: userForm.value.list,
    userRoleIdList: userForm.value.userGroupIdList as string[],
  };
  const res = await userApi.batchCreateUser(params);
  if (res.errorEmails) {
    const errData: Record<string, any> = {};
    Object.keys(res.errorEmails).forEach((key) => {
      const filedIndex = userForm.value.list.findIndex((e) => e.email === key);
      if (filedIndex > -1) {
        errData[`list[${filedIndex}].email`] = {
          status: 'error',
          message: t('system.user.createUserEmailExist'),
        };
      }
    });
    console.log(errData);
  } else {
    window.$message.success(t('system.user.addUserSuccess'));
    if (!isContinue) {
      cancelCreate();
    }
    await fetchData();
  }
}
const updateUser = async () => {
  const activeUser = userForm.value.list[0];
  if (activeUser) {
    const params = {
      id: activeUser.id as string,
      name: activeUser.name,
      nickName: activeUser.nickName,
      email: activeUser.email,
      phone: activeUser.phone,
      userRoleIdList: userForm.value.userGroup.map((e) => e.id),
    };
    await userApi.updateUserInfo(params);
    window.$message.success(t('system.user.updateUserSuccess'));
    cancelCreate()
    await fetchData();
  }
}
const beforeCreateUser = () => {
  if (userFormMode.value === 'create') {
    userFormValidate(createUser);
  } else {
    userFormValidate(updateUser);
  }
}
const saveAndContinue = () => {
  userFormValidate(async () => {
    await createUser(true);
    resetUserForm();
    batchFormRef.value?.resetForm();
  });
}
onMounted(() => {
  init()
  fetchData()
})
</script>

<template>
  <n-card>
    <w-data-table-tool-bar :add-permission="['SYSTEM_USER:READ+ADD']"
                           :add-icon="`i-solar:user-plus-linear`"
                           @refresh="fetchData"
                           @add="showUserModal('create')">
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
      <template #right>
        <n-input v-model:value="keyword" clearable :placeholder="t('system.user.searchUser')" size="small"/>
      </template>
    </w-data-table-tool-bar>
    <n-data-table :columns="columns"
                  :data="data"
                  :row-key="(row: UserListItem) => row.id"
                  @update:checked-row-keys="handleCheck"/>
    <w-pagination v-model:page="page" v-model:page-size="pageSize" :count="total || 0"/>
    <n-modal v-model:show="visible" preset="dialog" title="Dialog" :mask-closable="false" :closable="false"
             :style="{width: '40%'}">
      <template #header>
        <div>
          {{ userFormMode === 'create' ? t('system.user.createUserModalTitle') : t('system.user.editUserModalTitle') }}
        </div>
      </template>
      <n-alert type="info" :show-icon="false" class="mb-[16px]">{{ t('system.user.createUserTip') }}</n-alert>
      <div>
        <n-form v-if="visible" ref="userFormRef" :model="userForm" class="rounded-[4px]" label-placement="left">
          <w-batch-form ref="batchFormRef" :models="batchFormModels" :form-mode="userFormMode"
                        add-text="system.user.addUser" :default-vals="userForm.list"
                        max-height="250px"
                        @change="handleBatchFormChange"/>
          <n-form-item class="mb-0" path="userGroupIdList" :label="t('system.user.createUserUserGroup')"
                       :rule="{required: true, message: t('system.user.createUserUserGroupNotNull')}">
            <n-select v-model:value="userForm.userGroupIdList" :options="userGroupOptions" multiple
                      :placeholder="t('system.user.createUserUserGroupPlaceholder')"
                      class="w-full" label-field="name" value-field="id" clearable/>
          </n-form-item>
        </n-form>
      </div>
      <template #action>
        <n-button secondary :disabled="loading" @click="handleBeforeClose">{{
            t('system.user.editUserModalCancelCreate')
          }}
        </n-button>
        <n-button v-if="userFormMode === 'create'" secondary :loading="loading" @click="saveAndContinue">
          {{ t('system.user.editUserModalSaveAndContinue') }}
        </n-button>
        <n-button type="primary" :loading="loading" @click="beforeCreateUser">
          {{
            t(userFormMode === 'create' ? 'system.user.editUserModalCreateUser' : 'system.user.editUserModalEditUser')
          }}
        </n-button>
      </template>
    </n-modal>
  </n-card>
</template>

<style scoped>

</style>