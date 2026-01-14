<script setup lang="ts">
import { NButton, NCard, NForm, NFormItem, NInput, NCheckbox } from 'naive-ui'
import { reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const formValue = reactive({
  username: '',
  password: '',
  remember: false
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = () => {
  // 这里应该调用API进行登录验证
  console.log('登录信息：', formValue)
  // 登录成功后跳转到首页
  router.push('/')
}
</script>

<template>
  <div class="login-container">
    <n-card class="login-card" :bordered="false" size="small">
      <h2 class="title">登录</h2>
      <n-form :model="formValue" :rules="rules" class="login-form">
        <n-form-item path="username" first>
          <n-input
            v-model:value="formValue.username"
            placeholder="用户名"
            @keyup.enter="handleLogin"
          />
        </n-form-item>
        <n-form-item path="password" first>
          <n-input
            v-model:value="formValue.password"
            type="password"
            placeholder="密码"
            @keyup.enter="handleLogin"
          />
        </n-form-item>
        <div class="remember-forgot">
          <n-checkbox v-model:checked="formValue.remember">记住我</n-checkbox>
        </div>
        <n-button
          type="primary"
          :block="true"
          :loading="false"
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
  background: radial-gradient(circle, rgba(255,255,255,0.15) 0%, transparent 70%);
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