<script setup lang="ts">

import Logo from "/@/components/Logo.vue";
import NavEnd from "/@/layout/components/header/NavEnd.vue";
import WProjectSelect from "/@/components/WProjectSelect.vue";
</script>

<template>
  <n-layout-header bordered class="nav"
                   style="--side-padding: 32px; grid-template-columns: calc(272px - var(--side-padding)) 1fr auto;">
    <Logo/>
    <div>
      <w-project-select/>
    </div>
    <nav-end/>
  </n-layout-header>
</template>

<style scoped>
.nav {
  align-items: center;
}

.nav {
  display: grid;
  grid-template-rows: 63px;
  padding: 0 32px;
}
</style>