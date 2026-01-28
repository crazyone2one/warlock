<script setup lang="ts">
import type {CurrentUserGroupItem, UserGroupItem} from "/@/api/types/user-group.ts";
import {AuthScopeEnum, type AuthScopeEnumType} from "/@/utils/common-enum.ts";
import {computed, inject, ref} from "vue";
import {useI18n} from "vue-i18n";
import UserGroupPopover from "/@/views/setting/user-group/components/UserGroupPopover.vue";
import {hasAnyPermission} from "/@/utils/permission.ts";
import MMoreAction from "/@/components/MMoreAction.vue";
import {userGroupApi} from "/@/api/methods/userGroup.ts";

const {t} = useI18n();

const systemType = inject<AuthScopeEnumType>('systemType');
const props = defineProps<{
  addPermission: string[];
  updatePermission: string[];
  isGlobalDisable: boolean;
}>();

const emit = defineEmits<{
  (e: 'handleSelect', element: UserGroupItem): void;
  (e: 'addUserSuccess', id: string): void;
}>();
const userGroupList = ref<UserGroupItem[]>([]);
const currentItem = ref<CurrentUserGroupItem>({
  id: '',
  name: '',
  code: '',
  internal: false,
  type: AuthScopeEnum.SYSTEM
});
const currentId = ref('');

const showSystem = computed(() => systemType === AuthScopeEnum.SYSTEM);
const systemToggle = ref(true);
const systemUserGroupVisible = ref(false);
const systemUserGroupList = computed(() => {
  return userGroupList.value.filter((ele) => ele.type === AuthScopeEnum.SYSTEM);
});

const projectUserGroupVisible = ref(false);

const isSystemShowAll = computed(() => {
  return hasAnyPermission([...props.updatePermission, 'SYSTEM_USER_ROLE:READ+DELETE']);
});

const systemMoreAction = [
  {
    label: t('system.userGroup.rename'),
    danger: false,
    eventTag: 'rename',
    permission: props.updatePermission,
  }
]
// const {send: fetchUserGroupList} = useRequest(() => userGroupApi.getUserGroupList(), {immediate: false})
const initData = async (id?: string, isSelect = true) => {
  let res: UserGroupItem[] = [];
  if (systemType === AuthScopeEnum.SYSTEM && hasAnyPermission(['SYSTEM_USER_ROLE:READ'])) {
    res = await userGroupApi.getUserGroupList();
  }
  if (res.length > 0) {
    userGroupList.value = res
    if (isSelect) {
      if (id) {
        const item = res.find((i) => i.id === id);
        if (item) {
          handleListItemClick(item);
        } else {
          window.$message.warning(t('common.resourceDeleted'));
          handleListItemClick(res[0] as UserGroupItem);
        }
      } else {
        handleListItemClick(res[0] as UserGroupItem);
      }
    }
  }
}
const handleCreateUG = (scoped: AuthScopeEnumType) => {
  if (scoped === AuthScopeEnum.SYSTEM) {
    systemUserGroupVisible.value = true;
  } else {
    projectUserGroupVisible.value = true;
  }
}
const handleAddMember = () => {
  emit('addUserSuccess', currentItem.value.id);
}
const handleListItemClick = (element: UserGroupItem) => {
  const {id, name, type, internal, code} = element;
  currentItem.value = {id, name, type, internal, code};
  currentId.value = id;
  emit('handleSelect', element);
}
defineExpose({
  initData,
});
</script>

<template>
  <n-flex vertical class="px-[16px] pb-[16px]">
    <div class=" pb-[8px] pt-[16px]">
      <n-input :placeholder="t('system.userGroup.searchHolder')" clearable/>
    </div>
    <div v-if="showSystem" v-permission="['SYSTEM_USER_ROLE:READ']" class="mt-2">
      <n-flex justify="space-between" class="px-[4px] py-[7px]">
        <div class="flex flex-row items-center gap-1">
          <n-icon v-if="systemToggle" size="18">
            <div class="i-solar:alt-arrow-down-bold cursor-pointer" @click="systemToggle = false"/>
          </n-icon>
          <n-icon v-else size="16">
            <div class="i-solar:alt-arrow-up-bold cursor-pointer" @click="systemToggle = true"/>
          </n-icon>
          <div class="text-[14px]">
            {{ t('system.userGroup.systemUserGroup') }}
          </div>
        </div>
        <user-group-popover :list="systemUserGroupList" :visible="systemUserGroupVisible"
                            :auth-scope="AuthScopeEnum.SYSTEM"
                            @cancel="systemUserGroupVisible = false">
          <n-tooltip trigger="hover" placement="right">
            <template #trigger>
              <n-icon v-permission="props.addPermission" size="20">
                <div class="i-solar:add-circle-linear cursor-pointer" @click="handleCreateUG(AuthScopeEnum.SYSTEM)"/>
              </n-icon>
            </template>
            {{ `创建${t('system.userGroup.systemUserGroup')}` }}
          </n-tooltip>

        </user-group-popover>
      </n-flex>
      <Transition>
        <div v-if="systemToggle">
          <div v-for="(item) in systemUserGroupList" :key="item.id" class="list-item"
               :class="{'!bg-sky-500/50': item.id === currentId}"
               @click="handleListItemClick(item)">
            <user-group-popover :list="systemUserGroupList" :visible="false"
                                :auth-scope="item.type"
                                @cancel="systemUserGroupVisible = false">
              <div class="flex max-w-[100%] grow flex-row items-center justify-between">
                <div :class="{'!text-[#18a058]': item.id === currentId}" class="cursor-pointer">
                  {{ item.name }}
                </div>
                <div
                    v-if=" item.type === systemType || (isSystemShowAll && !item.internal && (item.scopeId !== 'global' || !isGlobalDisable) && systemMoreAction.length > 0)"
                    class="list-item-action flex flex-row items-center gap-[8px] opacity-0"
                    :class="{ '!opacity-100': item.id === currentId }">
                  <div v-if="item.type === systemType">
                    <n-icon v-permission="props.updatePermission" size="20">
                      <div class="i-solar:add-circle-linear cursor-pointer" @click="handleAddMember"/>
                    </n-icon>
                  </div>
                  <m-more-action
                      v-if="isSystemShowAll &&!item.internal &&(item.scopeId !== 'global' || !isGlobalDisable) && systemMoreAction.length > 0"
                      :list="systemMoreAction">

                  </m-more-action>
                </div>
              </div>

            </user-group-popover>
          </div>
          <n-divider class="my-[0px] mt-[6px]"/>
        </div>
      </Transition>
    </div>
    <!--    <div v-if="showProject" v-permission="['PROJECT_GROUP:READ']" class="mt-2">-->
    <!--      <n-flex justify="space-between" class="px-[4px] py-[7px]">-->
    <!--        <div class="flex flex-row items-center gap-1">-->
    <!--          <n-icon v-if="projectToggle" size="18">-->
    <!--            <div class="i-solar:alt-arrow-down-bold cursor-pointer" @click="projectToggle = false"/>-->
    <!--          </n-icon>-->
    <!--          <n-icon v-else size="16">-->
    <!--            <div class="i-solar:alt-arrow-up-bold cursor-pointer" @click="projectToggle = true"/>-->
    <!--          </n-icon>-->
    <!--          <div class="text-[14px]">-->
    <!--            {{ t('system.userGroup.projectUserGroup') }}-->
    <!--          </div>-->
    <!--        </div>-->
    <!--        <user-group-popover :list="projectUserGroupList" :visible="projectUserGroupVisible"-->
    <!--                            :auth-scope="AuthScopeEnum.PROJECT"-->
    <!--                            @cancel="projectUserGroupVisible = false">-->
    <!--          <n-tooltip trigger="hover" placement="right">-->
    <!--            <template #trigger>-->
    <!--              <n-icon v-permission="props.addPermission" size="20">-->
    <!--                <div class="i-solar:add-circle-linear cursor-pointer" @click="projectUserGroupVisible = true"/>-->
    <!--              </n-icon>-->
    <!--            </template>-->
    <!--            {{ `创建${t('system.userGroup.projectUserGroup')}`}}-->
    <!--          </n-tooltip>-->

    <!--        </user-group-popover>-->
    <!--      </n-flex>-->
    <!--      <Transition>-->
    <!--        <div v-if="projectToggle">-->
    <!--          <div v-for="(item) in projectUserGroupList" :key="item.id" class="list-item"-->
    <!--               :class="{'!bg-sky-500/50': item.id === currentId}"-->
    <!--               @click="handleListItemClick(item)">-->
    <!--            <user-group-popover :list="projectUserGroupList" :visible="false"-->
    <!--                                :auth-scope="item.type"-->
    <!--                                @cancel="projectUserGroupVisible = false">-->
    <!--              <div class="flex max-w-[100%] grow flex-row items-center justify-between">-->
    <!--                <div :class="{'!text-[#18a058]': item.id === currentId}" class="cursor-pointer">-->
    <!--                  {{ item.name }}-->
    <!--                </div>-->
    <!--                <div-->
    <!--                    v-if=" item.type === systemType || (isProjectShowAll && !item.internal && (item.scopeId !== 'global' || !isGlobalDisable) && projectMoreAction.length > 0)"-->
    <!--                    class="list-item-action flex flex-row items-center gap-[8px] opacity-0"-->
    <!--                    :class="{ '!opacity-100': item.id === currentId }">-->
    <!--                  <div v-if="item.type === systemType">-->
    <!--                    <n-icon v-permission="props.updatePermission" size="20">-->
    <!--                      <div class="i-solar:add-circle-linear" @click="handleAddMember"/>-->
    <!--                    </n-icon>-->
    <!--                  </div>-->
    <!--                  <m-more-action-->
    <!--                      v-if="isProjectShowAll &&!item.internal &&(item.scopeId !== 'global' || !isGlobalDisable) && projectMoreAction.length > 0"-->
    <!--                      :list="projectMoreAction">-->

    <!--                  </m-more-action>-->
    <!--                </div>-->
    <!--              </div>-->

    <!--            </user-group-popover>-->
    <!--          </div>-->
    <!--        </div>-->
    <!--      </Transition>-->
    <!--    </div>-->
  </n-flex>
</template>

<style scoped>
.list-item {
  padding: 7px 4px 7px 20px;
  height: 38px;
  border-radius: 4px;
  @apply flex cursor-pointer items-center hover:bg-[rgb(var(--primary-9))];

  &:hover .list-item-action {
    opacity: 1;
  }
}
</style>