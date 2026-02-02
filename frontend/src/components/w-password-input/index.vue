<script setup lang="ts">
import {NPopover} from "naive-ui";
import {useI18n} from "vue-i18n";
import {validatePasswordLength, validateWordPassword} from "/@/utils/validate.ts";
import {ref} from "vue";

const {t} = useI18n();
const password = defineModel<string>('password', {type: String, default: ''});
const pswValidateRes = ref(false);
const pswLengthValidateRes = ref(false);
const validatePsw = (value: string) => {
  pswValidateRes.value = validateWordPassword(value);
  pswLengthValidateRes.value = validatePasswordLength(value);
}
</script>

<template>
  <n-popover trigger="focus" placement="top-start">
    <template #trigger>
      <n-input type="password" v-model:value="password" :placeholder="t('ms.passwordInput.passwordPlaceholder')"
               clearable :maxlength="32" @input="validatePsw" @clear="validatePsw"/>
    </template>
    <div class="check-list-item">
      <template v-if="pswLengthValidateRes">
        <div class="i-solar:check-circle-bold-duotone text-green-6"/>
        {{ t('ms.passwordInput.passwordLengthRule') }}
      </template>
      <template v-else>
        <div class="i-solar:close-circle-bold-duotone text-red-6"/>
        {{ t('ms.passwordInput.passwordLengthRule') }}
      </template>
    </div>
    <div class="check-list-item">
      <template v-if="pswValidateRes">
        <div class="i-solar:check-circle-bold-duotone text-green-6"/>
        {{ t('ms.passwordInput.passwordWordRule', {symbol: '!@#$%^&*()_+.'}) }}
      </template>
      <template v-else>
        <div class="i-solar:close-circle-bold-duotone text-red-6"/>
        {{ t('ms.passwordInput.passwordWordRule', {symbol: '!@#$%^&*()_+.'}) }}
      </template>
    </div>
  </n-popover>
</template>

<style scoped>
.check-list-item {
  @apply flex items-center gap-2;

  &:first-child {
    @apply mt-2;
  }

  &:not(:last-child) {
    @apply mb-2;
  }
}
</style>