<script setup lang="ts">
import {h, ref} from 'vue'
import {type MenuOption} from 'naive-ui'
import {RouterLink, useRoute} from "vue-router";

const collapsed = ref(false)

const route = useRoute()
const activeKey = ref<string | null>(route.name as string)
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
</script>

<template>
  <n-layout-sider bordered collapse-mode="width"
                  :collapsed-width="60"
                  :width="220"
                  :collapsed="collapsed"
                  show-trigger
                  :native-scrollbar="false"
                  @collapse="collapsed = true"
                  @expand="collapsed = false">
    <n-menu v-model:value="activeKey"
            :options="menuOptions" :collapsed="collapsed"
            :collapsed-width="64"
            :collapsed-icon-size="22"
    />
  </n-layout-sider>
</template>

<style scoped>

</style>