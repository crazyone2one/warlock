<script setup lang="ts">
import {h, ref} from 'vue'
import {type MenuOption} from 'naive-ui'
import {RouterLink} from "vue-router";
import {listenerRouteChange} from "/@/utils/route-listener.ts";
import {useAppStore} from "/@/store";

const appStore = useAppStore()
const activeKey = ref<string | null>(null)
const renderIcon = (icon: string) => {
  return () => h('div', {class: icon})
}
const menuOptions: MenuOption[] = [
  {
    label: () => h(RouterLink, {to: {name: 'Dashboard'}}, {default: () => 'Dashboard'}),
    key: 'Dashboard',
    icon: renderIcon('i-solar:share-circle-linear')
  },
  {
    label: () => h(RouterLink, {to: {name: 'Schedule'}}, {default: () => 'Schedule'}),
    key: 'Schedule',
    icon: renderIcon('i-solar:document-medicine-linear')
  },
  {
    label: () => h(RouterLink, {to: {name: 'Project'}}, {default: () => 'Project'}),
    key: 'Project',
    icon: renderIcon('i-solar:file-text-outline')
  },
  {
    label: () => h(RouterLink, {to: {name: 'User'}}, {default: () => 'User'}),
    key: 'User',
    icon: renderIcon('i-solar:user-outline')
  },
]
listenerRouteChange((route) => {
  activeKey.value = route.name as string
})
</script>

<template>
  <n-layout-sider bordered
                  collapse-mode="width"
                  :collapsed-width="60"
                  :width="220"
                  :collapsed="appStore.getMenuCollapse"
                  show-trigger
                  :native-scrollbar="false"
                  @collapse="appStore.setMenuCollapse(true)"
                  @expand="appStore.setMenuCollapse(false)">
    <n-menu v-model:value="activeKey"
            :options="menuOptions"
            :collapsed="appStore.getMenuCollapse"
            :collapsed-width="64"
            :collapsed-icon-size="22"
    />
  </n-layout-sider>
</template>

<style scoped>

</style>