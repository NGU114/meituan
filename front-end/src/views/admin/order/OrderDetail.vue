<!-- src/views/admin/order/OrderDetail.vue -->
<template>
  <el-card>
    <el-descriptions title="订单详情" border>
      <el-descriptions-item label="订单ID">{{ detail.id }}</el-descriptions-item>
      <el-descriptions-item label="店铺">{{ detail.shopName }}</el-descriptions-item>
      <el-descriptions-item label="用户">{{ detail.userName }}</el-descriptions-item>
      <el-descriptions-item label="骑手">{{ detail.riderName || '未分配' }}</el-descriptions-item>
      <el-descriptions-item label="订单金额">¥{{ detail.totalAmount }}</el-descriptions-item>
      <el-descriptions-item label="订单状态">{{ detail.statusText || detail.status }}</el-descriptions-item>
      <el-descriptions-item label="下单时间">{{ detail.createdAt }}</el-descriptions-item>
      <el-descriptions-item label="收货地址" :span="2">{{ detail.address }}</el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <h4>商品列表</h4>
    <el-table :data="detail.items || []" border>
      <el-table-column prop="productName" label="商品名称" />
      <el-table-column prop="quantity" label="数量" />
      <el-table-column prop="price" label="单价" />
      <el-table-column prop="subtotal" label="小计" />
    </el-table>
    <el-button style="margin-top: 20px;" @click="$router.back()">返回</el-button>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getOrderDetail } from '@/api/admin/order'

const route = useRoute()
const detail = ref({})

const fetchDetail = async () => {
  const id = route.params.id
  try {
    detail.value = await getOrderDetail(id)
  } catch (err) {
    console.error('获取订单详情失败', err)
  }
}

onMounted(fetchDetail)
</script>