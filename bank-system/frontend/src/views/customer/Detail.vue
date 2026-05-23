<template>
  <div class="customer-detail">
    <div class="glass-card detail-card">
      <div class="detail-header">
        <span class="detail-title">客户详情</span>
      </div>
      <el-descriptions :column="2" border v-loading="loading">
        <el-descriptions-item label="客户ID">{{ customer.id }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ customer.name }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ customer.idCard }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ customer.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ customer.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="customer.status === 1 ? 'success' : 'danger'" size="small">
            {{ customer.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ customer.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ customer.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ customer.updateTime }}</el-descriptions-item>
      </el-descriptions>
      <div class="detail-actions">
        <el-button type="primary" @click="handleEdit">
          <el-icon><Edit /></el-icon> 编辑
        </el-button>
        <el-button @click="handleBack">
          <el-icon><List /></el-icon> 返回列表
        </el-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
export default { name: 'CustomerDetail' }
</script>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCustomerById } from '@/api/customer'
import { useTabsStore } from '@/stores/tabs'

const route = useRoute()
const tabsStore = useTabsStore()
const loading = ref(false)
const customer = ref<any>({})

onMounted(async () => {
  loading.value = true
  try {
    const res: any = await getCustomerById(Number(route.params.id))
    customer.value = res.data
  } finally {
    loading.value = false
  }
})

function handleEdit() {
  tabsStore.openTab({ path: `/customer/${route.params.id}/edit`, title: `编辑客户 #${route.params.id}`, icon: 'Edit' })
}

function handleBack() {
  tabsStore.closeTab(route.path)
  tabsStore.openTab({ path: '/customer/list', title: '客户列表', icon: 'List' })
}
</script>

<style scoped>
.customer-detail {
  animation: fadeSlideIn 0.35s ease-out;
}
.detail-card {
  padding: 24px;
  max-width: 800px;
}
.detail-header {
  margin-bottom: 20px;
}
.detail-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}
.detail-actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}
</style>
