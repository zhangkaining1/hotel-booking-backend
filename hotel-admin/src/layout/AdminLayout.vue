<template>
  <el-container class="admin-layout">
    <!-- 左侧菜单 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="admin-aside">
      <div class="logo-area">
        <h2 v-show="!isCollapse">酒店管理后台</h2>
        <h2 v-show="isCollapse">H</h2>
      </div>
      <el-menu
        :default-active="$route.path"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        background-color="#ffffff"
        text-color="#303133"
        active-text-color="var(--color-primary)"
        class="admin-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据概览</span>
        </el-menu-item>

        <el-sub-menu index="hotel">
          <template #title>
            <el-icon><OfficeBuilding /></el-icon>
            <span>酒店管理</span>
          </template>
          <el-menu-item index="/hotel/list">酒店列表</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="room">
          <template #title>
            <el-icon><House /></el-icon>
            <span>房间管理</span>
          </template>
          <el-menu-item index="/room/category">房间分类</el-menu-item>
          <el-menu-item index="/room/type">房型管理</el-menu-item>
          <el-menu-item index="/room/guest-room">客房管理</el-menu-item>
          <el-menu-item index="/room/price">价格管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="order">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/order/list">订单列表</el-menu-item>
          <el-menu-item index="/order/checkin">入住管理</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/review/list">
          <el-icon><ChatDotRound /></el-icon>
          <span>评价管理</span>
        </el-menu-item>

        <el-menu-item index="/user/list">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>

        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/admin">管理员管理</el-menu-item>
          <el-menu-item index="/system/role">角色管理</el-menu-item>
          <el-menu-item index="/system/resource">资源库管理</el-menu-item>
          <el-menu-item index="/system/config">系统配置</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <!-- 右侧区域 -->
    <el-container>
      <!-- 顶部栏 -->
      <el-header class="admin-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="admin-name">
              <el-icon><UserFilled /></el-icon>
              {{ adminStore.username || '管理员' }}
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区域 -->
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAdminStore } from '@/stores/user'

const router = useRouter()
const adminStore = useAdminStore()
const isCollapse = ref(false)

function handleLogout() {
  adminStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.admin-aside {
  background-color: #ffffff;
  overflow-y: auto;
  overflow-x: hidden;
  transition: width 0.3s;
  box-shadow: 2px 0 8px 0 rgba(29,35,41,.05);
  z-index: 10;
}

.logo-area {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary);
  font-size: 16px;
  border-bottom: 1px solid #f0f2f5;
  font-weight: bold;
}

.logo-area h2 {
  white-space: nowrap;
  overflow: hidden;
}

.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 0 20px;
  z-index: 9;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
  transition: color 0.3s;
}

.collapse-btn:hover {
  color: var(--color-primary);
}

.header-right {
  display: flex;
  align-items: center;
}

.admin-name {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #606266;
  font-size: 14px;
}
.admin-name:hover {
  color: var(--color-primary);
}

.admin-main {
  background-color: var(--color-bg-secondary);
  padding: 24px;
  overflow-y: auto;
}

/* 覆盖菜单样式让其更清新 */
.admin-menu {
  border-right: none;
}
.admin-menu .el-menu-item.is-active {
  background-color: var(--color-primary-light-9);
  border-right: 3px solid var(--color-primary);
}
</style>
