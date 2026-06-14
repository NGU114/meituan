<!-- src/views/admin/Dashboard.vue -->
<template>
  <div class="dashboard">
    <el-card>
      <el-row :gutter="20">
        <el-col :span="4">
          <div class="stat-card">
            <div class="label">用户总数</div>
            <div class="value">{{ data.userCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card">
            <div class="label">商家总数</div>
            <div class="value">{{ data.merchantCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card">
            <div class="label">骑手总数</div>
            <div class="value">{{ data.riderCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card">
            <div class="label">店铺总数</div>
            <div class="value">{{ data.shopCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card">
            <div class="label">订单总数</div>
            <div class="value">{{ data.orderCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card">
            <div class="label">总收入</div>
            <div class="value">¥{{ data.totalRevenue || 0 }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDashboard } from '@/api/admin/dashboard'

const data = ref({})

const fetchData = async () => {
  try {
    data.value = await getDashboard()
  } catch (err) {
    console.error('获取仪表盘数据失败', err)
  }
}

onMounted(fetchData)
</script>

<style scoped>
.dashboard {
  padding: 20px;
}
.stat-card {
  background: #f5f7fa;
  padding: 20px;
  text-align: center;
  border-radius: 8px;
}
.label {
  font-size: 14px;
  color: #666;
}
.value {
  font-size: 24px;
  font-weight: bold;
  margin-top: 8px;
}
</style>