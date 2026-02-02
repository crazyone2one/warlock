<script setup lang="ts">
import {useEventListener, useWindowSize} from '@vueuse/core';
import ProviderView from "/@/components/ProviderView.vue";
import BaseView from "/@/components/BaseView.vue";
import {useAppStore} from "/@/store";
import {authApi} from "/@/api/methods/auth.ts";
import {setLocalStorage} from "/@/utils/local-storage.ts";
import {onBeforeMount} from "vue";

const appStore = useAppStore();

// 获取公钥
const getPublicKey = async () => {
  const publicKey = await authApi.getPublicKey();
  setLocalStorage('salt', publicKey);
};

/** 屏幕大小改变时重新赋值innerHeight */
useEventListener(window, 'resize', () => {
  const {height} = useWindowSize();
  appStore.innerHeight = height.value;
});
onBeforeMount(async () => {
  await getPublicKey()
  const { height } = useWindowSize();
  appStore.innerHeight = height.value;
})
</script>

<template>
  <provider-view>
    <base-view/>
  </provider-view>

</template>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}

.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}

.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
