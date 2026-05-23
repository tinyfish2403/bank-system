<template>
  <div class="approval-detail">
    <div class="glass-card detail-card">
      <div class="detail-header">
        <span class="detail-title">授信批复详情</span>
      </div>
      <el-descriptions :column="2" border v-loading="loading">
        <el-descriptions-item label="批复编号">{{ approval.approvalNo }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ approval.customerName }}</el-descriptions-item>
        <el-descriptions-item label="批复金额">{{ approval.approvedAmount?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="授信额度">{{ approval.creditLimit?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="有效期起">{{ approval.startDate }}</el-descriptions-item>
        <el-descriptions-item label="有效期止">{{ approval.endDate }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType[approval.status]" size="small">{{ statusMap[approval.status] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="批复时间">{{ approval.approvalTime }}</el-descriptions-item>
        <el-descriptions-item label="批复意见" :span="2">{{ approval.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div class="detail-actions">
        <el-button @click="handleBack">
          <el-icon><List /></el-icon> 返回列表
        </el-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
export default { name: 'CreditApprovalDetail' }
</script>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getApprovalById } from '@/api/credit'
import { useTabsStore } from '@/stores/tabs'

const route = useRoute()
const tabsStore = useTabsStore()
const loading = ref(false)
const approval = ref<any>({})

const statusMap: Record<number, string> = { 1: '有效', 2: '已过期', 3: '已撤销' }
const statusType: Record<number, string> = { 1: 'success', 2: 'warning', 3: 'danger' }

onMounted(async () => {
  loading.value = true
  try {
    const res: any = await getApprovalById(Number(route.params.id))
    approval.value = res.data
  } finally { loading.value = false }
})

function handleBack() {
  tabsStore.closeTab(route.path)
  tabsStore.openTab({ path: '/credit/approval/list', title: '批复查询', icon: 'Checked' })
}
</script>

<style scoped>
.approval-detail { animation: fadeSlideIn 0.35s ease-out; }
.detail-card { padding: 24px; max-width: 800px; }
.detail-header { margin-bottom: 20px; }
.detail-title { font-size: 18px; font-weight: 600; color: var(--text-primary); }
.detail-actions { margin-top: 20px; display: flex; gap: 10px; }
</style>
