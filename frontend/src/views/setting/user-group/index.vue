<script setup lang="ts">
import UserGroupLeft from "/@/views/setting/user-group/components/UserGroupLeft.vue";
import {computed, nextTick, onMounted, provide, ref, watchEffect} from "vue";
import {AuthScopeEnum} from "/@/utils/common-enum.ts";
import type {CurrentUserGroupItem} from "/@/api/types/user-group.ts";
import {useRouter} from "vue-router";
import {useI18n} from "vue-i18n";
import AuthTable from "/@/views/setting/user-group/components/AuthTable.vue";
import UserTable from "/@/views/setting/user-group/components/UserTable.vue";
import {hasAnyPermission} from "/@/utils/permission.ts";

const {t} = useI18n();
const router = useRouter();
provide('systemType', AuthScopeEnum.SYSTEM);
const ugLeftRef = ref<InstanceType<typeof UserGroupLeft>>();
const userRef = ref<InstanceType<typeof UserTable>>();
const currentTable = ref('auth');
const keyword = ref('')
const currentUserGroupItem = ref<CurrentUserGroupItem>({
  id: '',
  name: '',
  code: '',
  type: AuthScopeEnum.SYSTEM,
  internal: true,
});
const handleSelect = (item: CurrentUserGroupItem) => {
  currentUserGroupItem.value = item;
};
const tableSearch = () => {
  if (currentTable.value === 'user' && userRef.value) {
    userRef.value.fetchData();
  } else if (!userRef.value) {
    nextTick(() => {
      userRef.value?.fetchData();
    });
  }
}
const handleAddMember = (id: string) => {
  if (id === currentUserGroupItem.value.id) {
    tableSearch();
  }
}
const couldShowUser = computed(() => currentUserGroupItem.value.type === AuthScopeEnum.SYSTEM);
watchEffect(() => {
  if (!couldShowUser.value) {
    currentTable.value = 'auth';
  } else {
    currentTable.value = 'auth';
  }
});
onMounted(() => {
  ugLeftRef.value?.initData(router.currentRoute.value.query.id as string, true);
});
</script>

<template>
  <n-card>
    <n-split direction="horizontal" :default-size="0.13">
      <template #1>
        <user-group-left ref="ugLeftRef" :add-permission="['SYSTEM_USER_ROLE:READ+ADD']"
                         :update-permission="['SYSTEM_USER_ROLE:READ+UPDATE']"
                         :is-global-disable="false"
                         @handle-select="handleSelect"
                         @add-user-success="handleAddMember"/>
      </template>
      <template #2>
        <div class="flex h-full flex-col overflow-hidden pt-[16px]">
          <div class="flex flex-row items-center justify-between px-[16px]">
            <n-radio-group v-if="couldShowUser" v-model:value="currentTable" name="radiobuttongroup1" class="mb-[16px]">
              <n-radio-button value="auth">{{ t('system.userGroup.auth') }}</n-radio-button>
              <n-radio-button value="user">{{ t('system.userGroup.user') }}</n-radio-button>
            </n-radio-group>
            <div class="flex items-center w-[240px]">
              <n-input v-if="currentTable === 'user'" :placeholder="t('system.user.searchUser')" clearable/>
            </div>
          </div>
          <div class="flex-1 overflow-hidden">
            <auth-table v-if="currentTable === 'auth'"
                        :current="currentUserGroupItem"
                        :save-permission="['SYSTEM_USER_ROLE:READ+UPDATE']"
                        :disabled="!hasAnyPermission(['SYSTEM_USER_ROLE:READ+UPDATE'])"/>
            <user-table ref="userRef" v-if="currentTable === 'user'" v-model:keyword="keyword"
                        :current="currentUserGroupItem"
                        :save-permission="['SYSTEM_USER_ROLE:READ+UPDATE']"
                        :disabled="!hasAnyPermission(['SYSTEM_USER_ROLE:READ+UPDATE'])"/>
          </div>
        </div>
      </template>
    </n-split>
  </n-card>
</template>

<style scoped>

</style>