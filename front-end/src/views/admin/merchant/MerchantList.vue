<!-- src/views/admin/merchant/MerchantList.vue -->
<template>
  <el-card>
    <el-button type="primary" @click="openAddDialog" style="margin-bottom: 10px;">新增商家</el-button>

    <el-table :data="list" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="商家名称" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="shopCount" label="店铺数" />
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
    <el-dialog v-model="dialogVisible" title="新增商家" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="商家名称">
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
import { getMerchantList, addMerchant } from '@/api/admin/merchant'

const list = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const form = ref({ name: '', phone: '' })

const fetchList = async () => {
  try {
    const res = await getMerchantList({ pageNum: pageNum.value, pageSize: pageSize.value })
    list.value = res.list
    total.value = res.total
  } catch (err) {
    console.error('获取商家列表失败', err)
  }
}

const openAddDialog = () => {
  form.value = { name: '', phone: '' }
  dialogVisible.value = true
}

const handleAdd = async () => {
  try {
    await addMerchant(form.value)
    ElMessage.success('新增成功')
    dialogVisible.value = false
    fetchList()
  } catch (err) {
    console.error('新增商家失败', err)
  }
}

onMounted(fetchList)
</script>