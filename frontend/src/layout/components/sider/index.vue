<script setup lang="ts">
import {h, ref} from 'vue'
import {type MenuOption} from 'naive-ui'

const collapsed = ref(true)
const renderIcon = (icon: string) => {
  return () => h('div', {class: icon})
}
const menuOptions: MenuOption[] = [
  {label: '且听风吟', key: 'hear-the-wind-sing', icon: renderIcon('i-solar:share-circle-linear')},
  {label: '1973年的弹珠玩具', key: 'pinball-1973', icon: renderIcon('i-solar:share-circle-linear')},
  {label: '寻羊冒险记', key: 'a-wild-sheep-chase', disabled: true, icon: renderIcon('i-solar:share-circle-linear')},
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
    <n-menu :options="menuOptions" :collapsed="collapsed"
            :collapsed-width="64"
            :collapsed-icon-size="22"
    />
  </n-layout-sider>
</template>

<style scoped>

</style>