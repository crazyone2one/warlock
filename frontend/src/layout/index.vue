<script setup lang="ts">
import Sider from '/@/layout/components/sider/index.vue'
</script>

<template>
  <n-layout style="height: 100vh;">
    <n-layout-header bordered style="height: 64px; padding: 10px; flex-shrink: 0;">
      颐和园路
    </n-layout-header>

    <n-layout has-sider style="height: calc(100vh - 64px - 64px);">
      <sider/>
      <n-layout-content content-style="padding: 12px; height: 100%;" :native-scrollbar="false">
        <router-view style="min-height: 100%;" />
      </n-layout-content>
    </n-layout>
    <n-layout-footer bordered style="height: 64px; padding: 10px; flex-shrink: 0;">footer</n-layout-footer>
  </n-layout>
</template>

<style scoped>

</style>