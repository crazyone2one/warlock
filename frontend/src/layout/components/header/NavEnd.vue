<script setup lang="ts">
import {h} from 'vue'
import {NAvatar, NText} from 'naive-ui'
import {useRequest} from "alova/client";
import {authApi} from "/@/api/methods/auth.ts";
import {useAppStore, useUserStore} from "/@/store";
import router from "/@/router";
import {useI18n} from "vue-i18n";
import {clearToken} from "/@/utils/auth.ts";
import {removeRouteListener} from "/@/utils/route-listener.ts";

const {t} = useI18n()
const appState = useAppStore()
const userState = useUserStore()
const renderCustomHeader = () => {
  return h(
      'div',
      {
        style: 'display: flex; align-items: center; padding: 8px 12px;'
      },
      [
        h(NAvatar, {
          round: true,
          style: 'margin-right: 12px;',
          src: 'https://07akioni.oss-cn-beijing.aliyuncs.com/demo1.JPG'
        }),
        h('div', null, [
          h('div', null, [h(NText, {depth: 2}, {default: () => '打工仔'})]),
          h('div', {style: 'font-size: 12px;'}, [
            h(
                NText,
                {depth: 3},
                {default: () => '毫无疑问，你是办公室里最亮的星'}
            )
          ])
        ])
      ]
  )
}
const dropOptions = [
  {
    key: 'header',
    type: 'render',
    render: renderCustomHeader
  },
  {
    key: 'header-divider',
    type: 'divider'
  },
  {
    label: '用户资料',
    key: 'profile',
    icon: () => h('div', {class: 'i-solar:user-id-linear'})
  },
  {
    label: '退出登录',
    key: 'logout',
    icon: () => h('div', {class: 'i-solar:logout-linear'})
  },
]
const {send} = useRequest(() => authApi.logout(), {immediate: false})
const handleSelect = (key: string) => {
  switch (key) {
    case 'profile':
      break;
    case 'logout':
      send().then(() => {
        appState.resetInfo()
        userState.resetInfo()
        clearToken()
        removeRouteListener()
        const currentRoute = router.currentRoute.value;
        window.$message.success(t('message.logoutSuccess'));
        router.push({
          name: 'login',
          query: {
            ...router.currentRoute.value.query,
            redirect: currentRoute.name as string,
          },
        });
      })
      break;
  }
}
</script>

<template>
  <n-flex>
    <n-button text>Button1</n-button>
    <n-button text>Button2</n-button>
    <n-button text>
      <template #icon>
        <n-icon>
          <div class="i-solar:bell-line-duotone"/>
        </n-icon>
      </template>
    </n-button>
    <n-dropdown trigger="click" :options="dropOptions" @select="handleSelect">
      <n-button text>
        <template #icon>
          <n-icon>
            <div class="i-solar:user-speak-linear"/>
          </n-icon>
        </template>
      </n-button>
    </n-dropdown>
  </n-flex>
</template>

<style scoped>
</style>