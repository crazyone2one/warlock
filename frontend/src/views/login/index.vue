<script setup lang="ts">
import {type FormInst} from 'naive-ui'
import {useRouter} from 'vue-router'
import {useForm} from "alova/client";
import {authApi} from "/@/api/methods/auth.ts";
import {ref} from "vue";
import {setToken} from "/@/utils/auth.ts";
import {useAppStore, useUserStore} from "/@/store";

const router = useRouter()
const formRef = ref<FormInst | null>(null)
const userStore = useUserStore()
const appStore = useAppStore()
const {send, form,loading} = useForm(formValue => authApi.login(formValue), {
  initialForm: {username: '', password: ''},
  immediate: false,
  resetAfterSubmiting: true
})
const rules = {
  username: [
    {required: true, message: '请输入用户名', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
  ]
}

const handleLogin = () => {
  formRef.value?.validate(error => {
    if (!error) {
      send().then(res => {
        const {accessToken, refreshToken} = res
        setToken(accessToken, refreshToken)
        authApi.getUserInfo().then(res => {
          userStore.setInfo(res)
          appStore.setCurrentProjectId(res.lastProjectId || '');
        })

        window.$message.success('登录成功');
        const {redirect, ...othersQuery} = router.currentRoute.value.query;
        router.push({
          name: redirect as string || 'Dashboard',
          query: {
            ...othersQuery,
          }
        })
      })
    }
  })
}
</script>

<template>
  <div class="login-container">
    <n-card class="login-card" :bordered="false" size="small">
      <h2 class="title">登录</h2>
      <n-form ref="formRef" :model="form" :rules="rules" class="login-form">
        <n-form-item path="username" first>
          <n-input
              v-model:value="form.username"
              placeholder="用户名"
              @keyup.enter="handleLogin"
          />
        </n-form-item>
        <n-form-item path="password" first>
          <n-input
              v-model:value="form.password"
              type="password"
              placeholder="密码"
              @keyup.enter="handleLogin"
          />
        </n-form-item>
        <!--        <div class="remember-forgot">-->
        <!--          <n-checkbox v-model:checked="formValue.remember">记住我</n-checkbox>-->
        <!--        </div>-->
        <n-button
            type="primary"
            :block="true"
            :loading="loading"
            @click="handleLogin"
        >
          登录
        </n-button>
      </n-form>
    </n-card>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #8598ef 0%, #c098e8 100%) fixed;
  position: relative;
  overflow: hidden;
}

.login-container::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.15) 0%, transparent 70%);
  animation: rotate 20s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.login-card {
  width: 400px;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  background-color: white;
}

.title {
  text-align: center;
  margin-bottom: 24px;
  color: #333;
  font-size: 24px;
  font-weight: 500;
}

.login-form {
  margin-top: 20px;
}

.remember-forgot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.n-button {
  margin-top: 10px;
}
</style>