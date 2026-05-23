<template>
  <div class="credit-app-detail">
    <div class="glass-card detail-card">
      <div class="detail-header">
        <span class="detail-title">授信申请详情</span>
      </div>
      <el-descriptions :column="2" border v-loading="loading">
        <el-descriptions-item label="申请编号">{{ app.applicationNo }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ app.customerName }}</el-descriptions-item>
        <el-descriptions-item label="授信金额">{{ app.creditAmount?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="授信类型">
          <el-tag size="small">{{ creditTypeMap[app.creditType] || app.creditType }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType[app.status]" size="small">{{ statusMap[app.status] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ app.applyTime }}</el-descriptions-item>
        <el-descriptions-item label="申请用途" :span="2">{{ app.purpose || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ app.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ app.updateTime }}</el-descriptions-item>
      </el-descriptions>
      <div class="detail-actions">
        <el-button v-if="app.status === 1" type="primary" @click="handleEdit">
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
export default { name: 'CreditApplicationDetail' }
</script>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getApplicationById } from '@/api/credit'
import { useTabsStore } from '@/stores/tabs'

const route = useRoute()
const tabsStore = useTabsStore()
const loading = ref(false)
const app = ref<any>({})

const creditTypeMap: Record<string, string> = { WORKING: '流动资金', LOAN: '贷款', GUARANTEE: '担保' }
const statusMap: Record<number, string> = { 1: '待审批', 2: '已批复', 3: '已拒绝' }
const statusType: Record<number, string> = { 1: 'warning', 2: 'success', 3: 'danger' }

onMounted(async () => {
  loading.value = true
  try {
    const res: any = await getApplicationById(Number(route.params.id))
    app.value = res.data
  } finally { loading.value = false }
})

function handleEdit() {
  tabsStore.openTab({ path: `/credit/application/${route.params.id}/edit`, title: `编辑申请 #${route.params.id}`, icon: 'Edit' })
}
function handleBack() {
  tabsStore.closeTab(route.path)
  tabsStore.openTab({ path: '/credit/application/list', title: '授信申请', icon: 'Document' })
}
</script>

<style scoped>
.credit-app-detail { animation: fadeSlideIn 0.35s ease-out; }
.detail-card { padding: 24px; max-width: 800px; }
.detail-header { margin-bottom: 20px; }
.detail-title { font-size: 18px; font-weight: 600; color: var(--text-primary); }
.detail-actions { margin-top: 20px; display: flex; gap: 10px; }
</style>
