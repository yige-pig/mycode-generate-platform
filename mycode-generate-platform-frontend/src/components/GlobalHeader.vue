<script setup lang="ts">
import { ref } from 'vue';

const menuItems = ref([
  { key: '1', label: '首页', path: '/' },
  { key: '2', label: '生成代码', path: '/generate' },
  { key: '3', label: '模板管理', path: '/templates' },
  { key: '4', label: '文档', path: '/docs' }
]);

const currentMenu = ref('1');

const handleMenuClick = (e: any) => {
  currentMenu.value = e.key;
  // 这里可以添加路由跳转逻辑
};
</script>

<template>
  <a-layout-header class="global-header">
    <div class="header-content">
      <div class="logo-section">
        <img src="../assets/logo.png" alt="Logo" class="logo" />
        <h1 class="site-title">零代码应用生成平台</h1>
      </div>

      <a-menu
        mode="horizontal"
        :selected-keys="[currentMenu]"
        @click="handleMenuClick"
        class="nav-menu"
      >
        <a-menu-item v-for="item in menuItems" :key="item.key">
          {{ item.label }}
        </a-menu-item>
      </a-menu>

      <div class="user-section">
        <a-button type="primary" ghost>
          登录
        </a-button>
      </div>
    </div>
  </a-layout-header>
</template>

<style scoped>
.global-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 24px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  width: 32px;
  height: 32px;
}

.site-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #1890ff;
}

.nav-menu {
  flex: 1;
  margin: 0 40px;
}

.user-section {
  display: flex;
  align-items: center;
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
  }

  .site-title {
    font-size: 16px;
  }

  .nav-menu {
    margin: 0 20px;
  }

  .a-menu-item {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .site-title {
    display: none;
  }

  .nav-menu {
    margin: 0 10px;
  }
}
</style>
