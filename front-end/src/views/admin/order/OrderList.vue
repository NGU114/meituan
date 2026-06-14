<!-- src/views/admin/order/OrderList.vue -->
<template>
  <el-card>
    <el-table :data="list" border style="width: 100%">
      <el-table-column prop="id" label="订单ID" width="100" />
      <el-table-column prop="shopName" label="店铺名称" />
      <el-table-column prop="userName" label="用户" />
      <el-table-column prop="totalAmount" label="订单金额" />
      <el-table-column prop="status" label="订单状态">
        <template #default="{ row }">
          <el-tag>{{ row.statusText || row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="下单时间" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="goDetail(row.id)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        @current-change="fetchList"
        @size-change="fetchList"
        style="margin-top: 16px; text-align: right;"
    />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderList } from '@/api/admin/order'

const router = useRouter()
const list = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchList = async () => {
  try {
    const res = await getOrderList({ pageNum: pageNum.value, pageSize: pageSize.value })
    list.value = res.list
    total.value = res.total
  } catch (err) {
    console.error('获取订单列表失败', err)
  }
}

const goDetail = (id) => {
  router.push(`/admin/order/detail/${id}`)
}

onMounted(fetchList)
</script>