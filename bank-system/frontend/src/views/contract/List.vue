<template>
  <div class="contract-list">
    <div class="glass-card search-card">
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="搜索客户姓名/合同编号" clearable style="width: 280px" @keyup.enter="handleSearch" />
        <el-select v-model="status" placeholder="状态筛选" clearable style="width: 140px">
          <el-option label="有效" :value="1" />
          <el-option label="已变更" :value="2" />
          <el-option label="已作废" :value="3" />
        </el-select>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon> 查询
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon> 重置
        </el-button>
        <div style="flex: 1" />
        <el-button type="primary" @click="openAddTab">
          <el-icon><Plus /></el-icon> 签订合同
        </el-button>
      </div>
    </div>

    <div class="glass-card table-card" style="margin-top: 12px">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="contractNo" label="合同编号" width="180" />
        <el-table-column prop="approvalNo" label="批复编号" width="180" />
        <el-table-column prop="customerName" label="客户" width="100" />
        <el-table-column prop="contractAmount" label="合同金额" width="120">
          <template #default="{ row }">{{ row.contractAmount?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="contractType" label="合同类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ contractTypeMap[row.contractType] || row.contractType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="signDate" label="签订日期" width="110" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType[row.status]" size="small">{{ statusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link type="primary" @click="openDetailTab(row.id)">详情</el-button>
            <el-button v-if="row.status === 1" size="small" link type="primary" @click="openEditTab(row.id)">变更</el-button>
            <el-button v-if="row.status === 1" size="small" link type="danger" @click="handleCancel(row)">作废</el-button>
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
export default { name: 'ContractList' }
</script>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getContractList, cancelContract } from '@/api/contract'
import { useTabsStore } from '@/stores/tabs'

const tabsStore = useTabsStore()
const loading = ref(false)
const tableData = ref<any[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const keyword = ref('')
const status = ref<number | undefined>(undefined)

const contractTypeMap: Record<string, string> = { CREDIT: '授信合同', LOAN: '贷款合同', GUARANTEE: '担保合同' }
const statusMap: Record<number, string> = { 1: '有效', 2: '已变更', 3: '已作废' }
const statusType: Record<number, string> = { 1: 'success', 2: 'warning', 3: 'danger' }

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res: any = await getContractList({ page: page.value, size: size.value, keyword: keyword.value, status: status.value })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

function handleSearch() { page.value = 1; fetchData() }
function handleReset() { keyword.value = ''; status.value = undefined; page.value = 1; fetchData() }
function openAddTab() { tabsStore.openTab({ path: '/contract/add', title: '签订合同', icon: 'EditPen' }) }
function openDetailTab(id: number) { tabsStore.openTab({ path: `/contract/${id}`, title: `合同详情 #${id}`, icon: 'View' }) }
function openEditTab(id: number) { tabsStore.openTab({ path: `/contract/${id}/edit`, title: `合同变更 #${id}`, icon: 'Edit' }) }

function handleCancel(row: any) {
  ElMessageBox.confirm(`确定要作废合同「${row.contractNo}」吗？作废后不可恢复。`, '确认作废', { type: 'warning' })
    .then(async () => {
      await cancelContract(row.id)
      ElMessage.success('合同已作废')
      fetchData()
    })
    .catch(() => {})
}
</script>

<style scoped>
.contract-list { animation: fadeSlideIn 0.35s ease-out; }
.search-card { padding: 16px; }
.search-bar { display: flex; align-items: center; gap: 10px; }
.table-card { padding: 0; overflow: hidden; }
.pagination-wrap { display: flex; justify-content: flex-end; padding: 12px 16px; }
</style>
