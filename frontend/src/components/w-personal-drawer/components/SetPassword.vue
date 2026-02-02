<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {ref} from "vue";
import {type FormInst} from "naive-ui";
import {validatePasswordLength, validateWordPassword} from "/@/utils/validate.ts";
import WPasswordInput from '/@/components/w-password-input/index.vue'
import {useRequest} from "alova/client";
import {userApi} from "/@/api/methods/user.ts";
import {useUserStore} from "/@/store";
import {clearToken} from "/@/utils/auth.ts";
import {useRouter} from "vue-router";

const {t} = useI18n()
const router = useRouter();
const counting = ref(3);
const formRef = ref<FormInst | null>(null)
const pswValidateRes = ref(false);
const pswLengthValidateRes = ref(false);
const userStore = useUserStore()
const form = ref({
  password: '',
  newPsw: '',
  confirmPsw: '',
});
const rules = {
  password: [{required: true, message: t('invite.passwordNotNull')}],
  newPsw: [
    {required: true, message: t('invite.passwordNotNull')},
    {
      validator: (_rule: any, value: string) => {
        pswValidateRes.value = validateWordPassword(value);
        pswLengthValidateRes.value = validatePasswordLength(value);
        if (!pswLengthValidateRes.value) {
          return new Error(t('invite.passwordLengthRule'))
        } else if (!pswValidateRes.value) {
          return new Error(t('invite.passwordWordRule'))
        }
      },
    },
  ],
  confirmPsw: [
    {required: true, message: t('invite.repasswordNotNull')},
    {
      validator: (_rule: any, value: string) => {
        if (value !== form.value.newPsw) {
          return new Error(t('invite.repasswordNotSame'))
        }
      },
    },
  ],
};
const {send, loading} = useRequest(formData => userApi.updatePsw(formData), {
  immediate: false
})
const handleChangePsw = () => {
  formRef.value?.validate(error => {
    if (!error) {
      console.log({
        id: userStore.id || '',
        // newPassword: encrypted(form.value.newPsw) || '',
        newPassword: form.value.newPsw,
        // oldPassword: encrypted(form.value.password) || '',
        oldPassword: form.value.password,
      })
      send({
        id: userStore.id || '',
        // newPassword: encrypted(form.value.newPsw) || '',
        newPassword: form.value.newPsw,
        // oldPassword: encrypted(form.value.password) || '',
        oldPassword: form.value.password,
      }).then(() => {
        window.$message.success(t('ms.personal.updatePswSuccess', {count: counting.value}), {
          duration: 1000
        })
        const timer = setInterval(() => {
          counting.value--;
          window.$message.success(t('ms.personal.updatePswSuccess', {count: counting.value}), {
            duration: 1000
          })
        }, 1000);
        setTimeout(() => {
          clearInterval(timer);
          clearToken()
          router.push({
            name: 'login',
            query: {
              ...router.currentRoute.value.query,
              redirect: router.currentRoute.value.name as string,
            },
          });
        }, 3000);
      })
    }
  })
}
</script>

<template>
  <div class="mb-[16px] flex items-center justify-between">
    <div class="font-medium">{{ t('ms.personal.setPsw') }}</div>
  </div>
  <n-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-placement="top"
      label-width="auto"
      require-mark-placement="right-hanging"
  >
    <n-form-item :label="t('ms.personal.currentPsw')" path="password">
      <n-input type="password" v-model:value="form.password" :placeholder="t('invite.passwordPlaceholder')" clearable/>
    </n-form-item>
    <n-form-item :label="t('ms.personal.newPsw')" path="newPsw">
      <w-password-input v-model:password="form.newPsw"/>
      <div class="mt-[4px] flex w-full items-center text-[12px] leading-[16px] text-[#9597a4]">
        {{ t('ms.personal.changePswTip') }}
      </div>
    </n-form-item>
    <n-form-item class="hidden-item" path="confirmPsw">
      <n-input type="password" v-model:value="form.confirmPsw" :placeholder="t('invite.passwordPlaceholder')"
               clearable/>
    </n-form-item>
    <n-form-item>
      <n-button type="primary" class="mt-[16px]" :loading="loading" @click="handleChangePsw">
        {{ t('common.update') }}
      </n-button>
    </n-form-item>
  </n-form>
</template>

<style scoped>

</style>