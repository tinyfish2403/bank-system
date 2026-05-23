<template>
  <div class="contract-detail">
    <div class="glass-card detail-card">
      <div class="detail-header">
        <span class="detail-title">合同详情</span>
      </div>
      <el-descriptions :column="2" border v-loading="loading">
        <el-descriptions-item label="合同编号">{{ contract.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="批复编号">{{ contract.approvalNo }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ contract.customerName }}</el-descriptions-item>
        <el-descriptions-item label="合同金额">{{ contract.contractAmount?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="合同类型">
          <el-tag size="small">{{ contractTypeMap[contract.contractType] || contract.contractType }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="签订日期">{{ contract.signDate }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType[contract.status]" size="small">{{ statusMap[contract.status] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ contract.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ contract.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ contract.updateTime }}</el-descriptions-item>
      </el-descriptions>
      <div class="detail-actions">
        <el-button v-if="contract.status === 1" type="primary" @click="handleEdit">
          <el-icon><Edit /></el-icon> 变更
        </el-button>
        <el-button @click="handleBack">
          <el-icon><List /></el-icon> 返回列表
        </el-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
export default { name: 'ContractDetail' }
</script>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getContractById } from '@/api/contract'
import { useTabsStore } from '@/stores/tabs'

const route = useRoute()
const tabsStore = useTabsStore()
const loading = ref(false)
const contract = ref<any>({})

const contractTypeMap: Record<string, string> = { CREDIT: '授信合同', LOAN: '贷款合同', GUARANTEE: '担保合同' }
const statusMap: Record<number, string> = { 1: '有效', 2: '已变更', 3: '已作废' }
const statusType: Record<number, string> = { 1: 'success', 2: 'warning', 3: 'danger' }

onMounted(async () => {
  loading.value = true
  try {
    const res: any = await getContractById(Number(route.params.id))
    contract.value = res.data
  } finally { loading.value = false }
})

function handleEdit() {
  tabsStore.openTab({ path: `/contract/${route.params.id}/edit`, title: `合同变更 #${route.params.id}`, icon: 'Edit' })
}
function handleBack() {
  tabsStore.closeTab(route.path)
  tabsStore.openTab({ path: '/contract/list', title: '合同列表', icon: 'Tickets' })
}
</script>

<style scoped>
.contract-detail { animation: fadeSlideIn 0.35s ease-out; }
.detail-card { padding: 24px; max-width: 800px; }
.detail-header { margin-bottom: 20px; }
.detail-title { font-size: 18px; font-weight: 600; color: var(--text-primary); }
.detail-actions { margin-top: 20px; display: flex; gap: 10px; }
</style>
