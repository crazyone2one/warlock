<script setup lang="ts">
import {computed, h, ref} from 'vue'
import {type MenuOption} from 'naive-ui'
import {type RouteMeta, type RouteRecordRaw, RouterLink} from "vue-router";
import {listenerRouteChange} from "/@/utils/route-listener.ts";
import {useAppStore} from "/@/store";
import useMenuTree from "/@/layout/components/sider/useMenuTree.ts";
import {generateMenus} from "/@/layout/components/sider/utils.ts";
import {useI18n} from "vue-i18n";

export interface IMenu {
  label: string
  key: string
  icon?: string
  name: string
  params?: { [key: string]: string }
  children?: IMenu[]
  meta?: RouteMeta
}

const {t} = useI18n()
const appStore = useAppStore()
const activeKey = ref<string | null>(null)

const {menuTree} = useMenuTree();
const menus = computed(() => {
  return generateMenus(menuTree.value as RouteRecordRaw[])
})
const options = computed(() => (menus.value ? mapping(menus.value) : []))
const mapping = (items: IMenu[]): MenuOption[] =>
    items.map(item => {
      return ({
        ...item,
        key: item.name,
        label: item.children ? t(item.label) :
            item.name != null ? () => h(RouterLink, {
              to:
                  {name: item.name}
            }, {default: () => t(item.label)}) : t(item.label),
        icon: item.icon != null ? () => h("div", {class: item.icon}) : undefined,
        children: item.children && mapping(item.children)
      })
    })
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
            :options="options"
            :collapsed="appStore.getMenuCollapse"
            :collapsed-width="64"
            :collapsed-icon-size="22"
    />
  </n-layout-sider>
</template>

<style scoped>

</style>