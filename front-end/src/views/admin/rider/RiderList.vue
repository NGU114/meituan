<!-- src/views/admin/rider/RiderList.vue -->
<template>
  <el-card>
    <el-button type="primary" @click="openAddDialog" style="margin-bottom: 10px;">新增骑手</el-button>

    <el-table :data="list" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="骑手姓名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'warning'">
            {{ row.status === 1 ? '在岗' : '休息' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" />
    </el-table>

    <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        @current-change="fetchList"
        @size-change="fetchList"
        style="margin-top: 16px; text-align: right;"
    />

    <!-- 新增弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增骑手" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="骑手姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getRiderList, addRider } from '@/api/admin/rider'

const list = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const form = ref({ name: '', phone: '' })

const fetchList = async () => {
  try {
    const res = await getRiderList({ pageNum: pageNum.value, pageSize: pageSize.value })
    list.value = res.list
    total.value = res.total
  } catch (err) {
    console.error('获取骑手列表失败', err)
  }
}

const openAddDialog = () => {
  form.value = { name: '', phone: '' }
  dialogVisible.value = true
}

const handleAdd = async () => {
  try {
    await addRider(form.value)
    ElMessage.success('新增成功')
    dialogVisible.value = false
    fetchList()
  } catch (err) {
    console.error('新增骑手失败', err)
  }
}

onMounted(fetchList)
</script>