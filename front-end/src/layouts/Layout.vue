<!-- src/layouts/Layout.vue -->
<template>
  <el-container style="height: 100vh;">
    <!-- 侧边栏 -->
    <el-aside width="200px">
      <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical-demo"
          @open="handleOpen"
          @close="handleClose"
          router
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/admin/user/list">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/merchant/list">
          <el-icon><Shop /></el-icon>
          <span>商家管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/rider/list">
          <el-icon><Van /></el-icon>
          <span>骑手管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/shop/list">
          <el-icon><Location /></el-icon>
          <span>店铺管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/order/list">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 头部 -->
      <el-header style="text-align: right; font-size: 12px;">
        <el-dropdown>
          <el-icon style="margin-right: 8px;"><Setting /></el-icon>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <!-- 主内容区 -->
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import {
  DataLine, User, Shop, Van, Location, Document, Setting
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = route.path

const handleOpen = (key, keyPath) => {
  console.log(key, keyPath)
}
const handleClose = (key, keyPath) => {
  console.log(key, keyPath)
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.el-header {
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
  line-height: 60px;
}
.el-aside {
  background-color: #304156;
}
</style>