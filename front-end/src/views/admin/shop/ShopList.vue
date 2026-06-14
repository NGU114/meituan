<!-- src/views/admin/shop/ShopList.vue -->
<template>
  <el-card>
    <el-table :data="list" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="店铺名称" />
      <el-table-column prop="merchantName" label="所属商家" />
      <el-table-column prop="status" label="营业状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '营业中' : '已关闭' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="goDetail(row.id)">查看</el-button>
          <el-button
              :type="row.status === 1 ? 'danger' : 'success'"
              size="small"
              @click="toggleStatus(row)"
          >
            {{ row.status === 1 ? '关闭' : '营业' }}
          </el-button>
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
import { ElMessage } from 'element-plus'
import { getShopList, updateShopStatus } from '@/api/admin/shop'

const router = useRouter()
const list = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchList = async () => {
  try {
    const res = await getShopList({ pageNum: pageNum.value, pageSize: pageSize.value })
    list.value = res.list
    total.value = res.total
  } catch (err) {
    console.error('获取店铺列表失败', err)
  }
}

const goDetail = (id) => {
  router.push(`/admin/shop/detail/${id}`)
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await updateShopStatus(row.id, { status: newStatus })
    ElMessage.success('状态修改成功')
    fetchList()
  } catch (err) {
    console.error('修改店铺状态失败', err)
  }
}

onMounted(fetchList)
</script>