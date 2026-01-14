<script setup lang="ts">
import Sider from '/@/layout/components/sider/index.vue'
</script>

<template>
  <n-layout has-sider>
    <sider/>
    <n-layout>
      <n-layout-header>颐和园路</n-layout-header>
      <n-layout-content content-style="padding: 24px;">
        <router-view/>
      </n-layout-content>
      <n-layout-footer>成府路</n-layout-footer>
    </n-layout>
  </n-layout>
</template>

<style scoped>

</style>