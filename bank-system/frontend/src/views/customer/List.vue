<template>
  <div class="customer-list">
    <div class="glass-card search-card">
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="搜索姓名/手机号/身份证号" clearable style="width: 320px" @keyup.enter="handleSearch" />
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon> 查询
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon> 重置
        </el-button>
        <div style="flex: 1" />
        <el-button type="primary" @click="openAddTab">
          <el-icon><Plus /></el-icon> 新增客户
        </el-button>
      </div>
    </div>

    <div class="glass-card table-card" style="margin-top: 12px">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="姓名" width="110" />
        <el-table-column prop="idCard" label="身份证号" width="190" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="170" />
        <el-table-column prop="status" label="状态" width="70">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link type="primary" @click="openDetailTab(row.id)">详情</el-button>
            <el-button size="small" link type="primary" @click="openEditTab(row.id)">编辑</el-button>
            <el-button size="small" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @current-change="fetchData"
          @size-change="fetchData"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
export default { name: 'CustomerList' }
</script>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCustomerList, deleteCustomer } from '@/api/customer'
import { useTabsStore } from '@/stores/tabs'

const tabsStore = useTabsStore()
const loading = ref(false)
const tableData = ref<any[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const keyword = ref('')

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res: any = await getCustomerList({ page: page.value, size: size.value, keyword: keyword.value })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  fetchData()
}

function handleReset() {
  keyword.value = ''
  page.value = 1
  fetchData()
}

function openAddTab() {
  tabsStore.openTab({ path: '/customer/add', title: '新增客户', icon: 'Plus' })
}

function openDetailTab(id: number) {
  tabsStore.openTab({ path: `/customer/${id}`, title: `客户详情 #${id}`, icon: 'View' })
}

function openEditTab(id: number) {
  tabsStore.openTab({ path: `/customer/${id}/edit`, title: `编辑客户 #${id}`, icon: 'Edit' })
}

function handleDelete(row: any) {
  ElMessageBox.confirm(`确定要删除客户「${row.name}」吗？`, '确认删除', { type: 'warning' })
    .then(async () => {
      await deleteCustomer(row.id)
      ElMessage.success('删除成功')
      fetchData()
    })
    .catch(() => {})
}
</script>

<style scoped>
.customer-list {
  animation: fadeSlideIn 0.35s ease-out;
}
.search-card {
  padding: 16px;
}
.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}
.table-card {
  padding: 0;
  overflow: hidden;
}
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  padding: 12px 16px;
}
</style>
