<!-- src/views/admin/shop/ShopDetail.vue -->
<template>
  <el-card>
    <el-descriptions title="店铺详情" border>
      <el-descriptions-item label="店铺ID">{{ detail.id }}</el-descriptions-item>
      <el-descriptions-item label="店铺名称">{{ detail.name }}</el-descriptions-item>
      <el-descriptions-item label="所属商家">{{ detail.merchantName }}</el-descriptions-item>
      <el-descriptions-item label="地址">{{ detail.address }}</el-descriptions-item>
      <el-descriptions-item label="营业状态">
        <el-tag :type="detail.status === 1 ? 'success' : 'danger'">
          {{ detail.status === 1 ? '营业中' : '已关闭' }}
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
import { getShopDetail } from '@/api/admin/shop'

const route = useRoute()
const detail = ref({})

const fetchDetail = async () => {
  const id = route.params.id
  try {
    detail.value = await getShopDetail(id)
  } catch (err) {
    console.error('获取店铺详情失败', err)
  }
}

onMounted(fetchDetail)
</script>