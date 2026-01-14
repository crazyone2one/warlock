<script setup lang="ts">
import BaseHeader from '/@/layout/components/header/index.vue'
import BaseSider from '/@/layout/components/sider/index.vue'
</script>

<template>
  <n-layout style="height: 100vh;">
    <base-header/>
    <n-layout has-sider style="height: calc(100vh - 64px - 64px);">
      <base-sider/>
      <n-layout-content  :native-scrollbar="false">
        <router-view style="min-height: 100%;"/>
      </n-layout-content>
    </n-layout>
<!--    <n-layout-footer bordered style="height: 64px; padding: 10px; flex-shrink: 0;">footer</n-layout-footer>-->
  </n-layout>
</template>

<style scoped>

</style>