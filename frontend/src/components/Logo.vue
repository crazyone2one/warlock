<template>
  <div class="logo-container">
    <img :src="logoSrc" alt="Logo" class="logo-img" />
  </div>
</template>

<script setup lang="ts">
import logoSrc from '/@/assets/logo.png'
</script>

<style scoped>
.logo-container {
  display: flex;
  cursor: pointer;
  font-size: 18px;
}

.logo-img {
  max-height: 30px;
  width: auto;
  object-fit: contain;
}
</style>