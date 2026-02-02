<script setup lang="ts">
import WDrawer from '/@/components/w-drawer/index.vue'
import {useI18n} from "vue-i18n";
import {ref} from "vue";
import WMenuPanel from "/@/components/WMenuPanel.vue";
import BaseInfo from "/@/components/w-personal-drawer/components/BaseInfo.vue";
import SetPassword from "/@/components/w-personal-drawer/components/SetPassword.vue";
import ApiKey from "/@/components/w-personal-drawer/components/ApiKey.vue";
import LocalExec from "/@/components/w-personal-drawer/components/LocalExec.vue";
import ModelConfig from "/@/components/w-personal-drawer/components/ModelConfig.vue";

const show = defineModel<boolean>('show', {type: Boolean, default: false});
const {t} = useI18n();
const activeMenu = ref('baseInfo');
const menuList = ref([
  {
    name: 'personal',
    title: t('ms.personal.info'),
    level: 1,
  },
  {
    name: 'baseInfo',
    title: t('ms.personal.baseInfo'),
    level: 2,
  },
  {
    name: 'setPsw',
    title: t('ms.personal.setPsw'),
    level: 2,
  },
  {
    name: 'setting',
    title: t('ms.personal.setting'),
    level: 1,
  },
  {
    name: 'apiKey',
    title: t('ms.personal.apiKey'),
    level: 2,
    permission: ['SYSTEM_PERSONAL_API_KEY:READ'],
  },
  {
    name: 'local',
    title: t('ms.personal.localExecution'),
    level: 2,
  },
  {
    name: 'modelConfig',
    title: t('system.config.modelConfig.modelConfigSet'),
    level: 2,
  },
]);
</script>

<template>
  <w-drawer v-model:show="show" :title="t('ms.personal')"
            :width="800"
            :footer="false">
    <div class="flex h-full w-full">
      <div class="h-full w-[208px] bg-[var(--color-text-n9)]">
        <w-menu-panel :menu-list="menuList" :default-key="activeMenu"
                      active-class="bg-transparent font-medium"
                      class="h-full !rounded-none bg-[var(--color-text-n9)] p-[16px_24px]"
                      @toggle-menu="(val) => (activeMenu = val)"/>
      </div>
      <div :class="`w-[calc(100%-208px)] 'p-[24px]'`">
        <base-info v-if="activeMenu === 'baseInfo'"/>
        <set-password v-else-if="activeMenu === 'setPsw'"/>
        <api-key v-else-if="activeMenu === 'apiKey'"/>
        <local-exec v-else-if="activeMenu === 'local'"/>
        <model-config v-else-if="activeMenu === 'modelConfig'" model-key="personal"/>
      </div>
    </div>
  </w-drawer>
</template>

<style scoped>

</style>