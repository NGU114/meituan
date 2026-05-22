<!-- src/views/admin/user/UserDetail.vue -->
<template>
  <el-card>
    <el-descriptions title="用户详情" border>
      <el-descriptions-item label="用户ID">{{ detail.id }}</el-descriptions-item>
      <el-descriptions-item label="账号">{{ detail.username }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ detail.phone }}</el-descriptions-item>
      <el-descriptions-item label="角色">{{ detail.role }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="detail.status === 1 ? 'success' : 'danger'">
          {{ detail.status === 1 ? '正常' : '禁用' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ detail.createdAt }}</el-descriptions-item>
    </el-descriptions>
    <el-button style="margin-top: 20px;" @click="$router.back()">返回</el-button>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getUserDetail } from '@/api/admin/user'

const route = useRoute()
const detail = ref({})

const fetchDetail = async () => {
  const id = route.params.id
  try {
    detail.value = await getUserDetail(id)
  } catch (err) {
    console.error('获取用户详情失败', err)
  }
}

onMounted(fetchDetail)
</script>