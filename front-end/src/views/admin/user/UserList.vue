<!-- src/views/admin/user/UserList.vue -->
<template>
  <el-card>
    <el-table :data="list" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="账号" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="role" label="角色" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" />
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
import { getUserList } from '@/api/admin/user'

const router = useRouter()
const list = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchList = async () => {
  try {
    const res = await getUserList({ pageNum: pageNum.value, pageSize: pageSize.value })
    list.value = res.list
    total.value = res.total
  } catch (err) {
    console.error('获取用户列表失败', err)
  }
}

const goDetail = (id) => {
  router.push(`/admin/user/detail/${id}`)
}

onMounted(fetchList)
</script>