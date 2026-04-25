<template>
  <a-layout-header class="header">
    <a-row :wrap="false">
      <!-- 左侧：Logo和标题 -->
      <a-col flex="200px">
        <RouterLink to="/">
          <div class="header-left">
            <img class="logo" src="@/assets/logo.png" alt="Logo" />
            <h1 class="site-title">应用生成</h1>
          </div>
        </RouterLink>
      </a-col>
      <!-- 中间：导航菜单 -->
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          :items="menuItems"
          @click="handleMenuClick"
        />
      </a-col>
      <!-- 右侧：用户操作区域 -->
      <a-col>
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id">
            <a-dropdown>
              <a-space>
                <a-avatar :src="loginUserStore.loginUser.userAvatar" />
                {{ loginUserStore.loginUser.userName ?? '无名' }}
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="doLogout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          <div v-else>
            <a-button type="primary" href="/user/login">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </a-layout-header>
</template>

<script setup lang="ts">
import { computed, h, ref } from 'vue'
import { useRouter } from 'vue-router'
import { type MenuProps, message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { userLogout } from '@/api/userController.ts'
import { LogoutOutlined, HomeOutlined } from '@ant-design/icons-vue'

const loginUserStore = useLoginUserStore()
const router = useRouter()
// 当前选中菜单
const selectedKeys = ref<string[]>(['/'])
// 监听路由变化，更新当前选中菜单
router.afterEach((to, from, next) => {
  selectedKeys.value = [to.path]
})

// 菜单配置项
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: '/admin/appManage',
    label: '应用管理',
    title: '应用管理',
  },

]

// 过滤菜单项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    const menuKey = menu?.key as string
    if (menuKey?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

// 展示在菜单的路由数组
const menuItems = computed<MenuProps['items']>(() => filterMenus(originItems))

// 处理菜单点击
const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  selectedKeys.value = [key]
  // 跳转到对应页面
  if (key.startsWith('/')) {
    router.push(key)
  }
}

// 退出登录
const doLogout = async () => {
  const res = await userLogout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}
</script>

<style scoped>
.header {
  background: rgba(15, 23, 42, 0.95);
  backdrop-filter: blur(20px);
  padding: 0 24px;
  position: relative;
  z-index: 100;
  border-bottom: 1px solid rgba(59, 130, 246, 0.3);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  height: 48px;
  width: 48px;
}

.site-title {
  margin: 0;
  font-size: 18px;
  color: #60a5fa;
  text-shadow: 0 0 10px rgba(59, 130, 246, 0.5);
}

.ant-menu-horizontal {
  border-bottom: none !important;
  background: transparent !important;
}

.ant-menu {
  background: transparent !important;
  color: #94a3b8 !important;
}

.ant-menu-item {
  color: #94a3b8 !important;
}

.ant-menu-item:hover {
  color: #60a5fa !important;
}

.ant-menu-item-selected {
  color: #60a5fa !important;
  background: rgba(59, 130, 246, 0.2) !important;
}

.ant-menu-item-selected::after {
  border-bottom: 2px solid #60a5fa !important;
}

.user-login-status {
  color: #94a3b8;
}

.user-login-status .ant-btn-primary {
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%) !important;
  border: none !important;
}

/* 下拉菜单样式 */
.ant-dropdown-menu {
  background: rgba(15, 23, 42, 0.95) !important;
  border: 1px solid rgba(59, 130, 246, 0.3) !important;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.4) !important;
}

.ant-dropdown-menu-item {
  color: #94a3b8 !important;
}

.ant-dropdown-menu-item:hover {
  background: rgba(59, 130, 246, 0.2) !important;
  color: #60a5fa !important;
}

.ant-dropdown-menu-item .anticon {
  color: #94a3b8 !important;
}

.ant-dropdown-menu-item:hover .anticon {
  color: #60a5fa !important;
}

/* 确保所有Ant Design组件使用深色主题 */
.ant-layout-header {
  background: transparent !important;
}

.ant-space-item {
  color: #94a3b8 !important;
}

.ant-avatar {
  background: rgba(59, 130, 246, 0.3) !important;
  border: 1px solid rgba(59, 130, 246, 0.5) !important;
}
</style>
