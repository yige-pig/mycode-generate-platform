<template>
  <div id="userLoginPage">
    <div class="login-container">
      <div class="login-header">
        <h2 class="title">AI 应用生成平台</h2>
        <p class="subtitle">不写一行代码，生成完整应用</p>
      </div>
      <div class="login-form-wrapper">
        <h3 class="form-title">用户登录</h3>
        <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
          <a-form-item name="userAccount" :rules="{ required: true, message: '请输入账号' }">
            <a-input v-model:value="formState.userAccount" placeholder="请输入账号" size="large" />
          </a-form-item>
          <a-form-item
            name="userPassword"
            :rules="[
              { required: true, message: '请输入密码' },
              { min: 8, message: '密码长度不能小于 8 位' },
            ]"
          >
            <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" size="large" />
          </a-form-item>
          <div class="tips">
            没有账号
            <RouterLink to="/user/register">去注册</RouterLink>
          </div>
          <a-form-item>
            <a-button type="primary" html-type="submit" style="width: 100%" size="large">登录</a-button>
          </a-form-item>
        </a-form>
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from 'vue'
import { userLogin } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

const router = useRouter()
const loginUserStore = useLoginUserStore()

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  const res = await userLogin(values)
  // 登录成功，把登录态保存到全局状态中
  if (res.data.code === 0 && res.data.data) {
    await loginUserStore.fetchLoginUser()
    message.success('登录成功')
    router.push({
      path: '/',
      replace: true,
    })
  } else {
    message.error('登录失败，' + res.data.message)
  }
}
</script>

<style scoped>
#userLoginPage {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  position: relative;
}

#userLoginPage::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    linear-gradient(180deg, rgba(10, 14, 23, 0.97) 0%, rgba(15, 23, 42, 0.95) 100%),
    radial-gradient(ellipse at 20% 0%, rgba(59, 130, 246, 0.1) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 100%, rgba(139, 92, 246, 0.08) 0%, transparent 50%);
  pointer-events: none;
  z-index: -1;
}

#userLoginPage::after {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    linear-gradient(rgba(59, 130, 246, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(59, 130, 246, 0.03) 1px, transparent 1px);
  background-size: 50px 50px;
  pointer-events: none;
  z-index: -1;
}

.login-container {
  width: 100%;
  max-width: 420px;
  padding: 24px;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-header .title {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #60a5fa 0%, #a78bfa 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
}

.login-header .subtitle {
  color: #64748b;
  font-size: 14px;
  margin: 0;
}

.login-form-wrapper {
  background: rgba(15, 23, 42, 0.8);
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: 16px;
  padding: 32px 24px;
  backdrop-filter: blur(10px);
}

.form-title {
  color: #e2e8f0;
  font-size: 18px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 24px;
}

.tips {
  text-align: center;
  color: #64748b;
  font-size: 14px;
  margin-bottom: 24px;
}

.tips a {
  color: #60a5fa;
  text-decoration: none;
  margin-left: 4px;
  transition: color 0.3s;
}

.tips a:hover {
  color: #93c5fd;
}

:deep(.ant-input) {
  background: rgba(15, 23, 42, 0.9) !important;
  border-color: rgba(59, 130, 246, 0.3) !important;
  color: #e2e8f0 !important;
  border-radius: 10px !important;
  height: 44px !important;
}

:deep(.ant-input::placeholder) {
  color: #64748b !important;
}

:deep(.ant-input:focus) {
  border-color: rgba(59, 130, 246, 0.6) !important;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1) !important;
}

:deep(.ant-input-password) {
  background: rgba(15, 23, 42, 0.9) !important;
  border-color: rgba(59, 130, 246, 0.3) !important;
  border-radius: 10px !important;
}

:deep(.ant-input-password .ant-input) {
  background: transparent !important;
  border: none !important;
  height: 42px !important;
}

:deep(.ant-input-password-icon) {
  color: #64748b !important;
}

:deep(.ant-input-password-icon:hover) {
  color: #94a3b8 !important;
}

:deep(.ant-form-item) {
  margin-bottom: 20px;
}

:deep(.ant-form-item-label > label) {
  color: #94a3b8 !important;
}

:deep(.ant-btn-primary) {
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%) !important;
  border: none !important;
  border-radius: 10px !important;
  height: 44px !important;
  font-weight: 600 !important;
  font-size: 16px !important;
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3) !important;
  transition: all 0.3s !important;
}

:deep(.ant-btn-primary:hover) {
  background: linear-gradient(135deg, #2563eb 0%, #7c3aed 100%) !important;
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4) !important;
  transform: translateY(-1px);
}

:deep(.ant-btn-primary:active) {
  transform: translateY(0);
}

:deep(.ant-form-item-explain-error) {
  color: #f87171 !important;
  font-size: 12px;
  margin-top: 4px;
}
</style>
