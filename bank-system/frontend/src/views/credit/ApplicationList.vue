<template>
  <div class="credit-app-list">
    <div class="glass-card search-card">
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="搜索客户姓名/申请编号" clearable style="width: 280px" @keyup.enter="handleSearch" />
        <el-select v-model="status" placeholder="状态筛选" clearable style="width: 140px">
          <el-option label="待审批" :value="1" />
          <el-option label="已批复" :value="2" />
          <el-option label="已拒绝" :value="3" />
        </el-select>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon> 查询
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon> 重置
        </el-button>
        <div style="flex: 1" />
        <el-button type="primary" @click="openAddTab">
          <el-icon><Plus /></el-icon> 新增授信申请
        </el-button>
      </div>
    </div>

    <div class="glass-card table-card" style="margin-top: 12px">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="applicationNo" label="申请编号" width="180" />
        <el-table-column prop="customerName" label="客户" width="100" />
        <el-table-column prop="creditAmount" label="授信金额" width="120">
          <template #default="{ row }">{{ row.creditAmount?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="creditType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ creditTypeMap[row.creditType] || row.creditType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType[row.status]" size="small">{{ statusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="170" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link type="primary" @click="openDetailTab(row.id)">详情</el-button>
            <el-button v-if="row.status === 1" size="small" link type="primary" @click="openEditTab(row.id)">编辑</el-button>
            <el-button v-if="row.status === 1" size="small" link type="success" @click="openApproveDialog(row)">审批</el-button>
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

    <!-- 审批对话框 -->
    <el-dialog v-model="approveDialogVisible" title="审批授信申请" width="520px" destroy-on-close>
      <el-form ref="approveFormRef" :model="approveForm" :rules="approveRules" label-width="100px">
        <el-form-item label="申请编号">
          <span class="form-text">{{ currentApp?.applicationNo }}</span>
        </el-form-item>
        <el-form-item label="申请金额">
          <span class="form-text">{{ currentApp?.creditAmount?.toLocaleString() }}</span>
        </el-form-item>
        <el-form-item label="批复金额" prop="approvedAmount">
          <el-input-number v-model="approveForm.approvedAmount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="授信额度" prop="creditLimit">
          <el-input-number v-model="approveForm.creditLimit" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="有效期" prop="startDate">
          <el-date-picker v-model="approveForm.startDate" type="date" placeholder="开始日期" value-format="YYYY-MM-DD" />
          <span style="margin: 0 8px">至</span>
          <el-date-picker v-model="approveForm.endDate" type="date" placeholder="结束日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="批复意见" prop="remark">
          <el-input v-model="approveForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="approving" @click="handleApprove">确认审批</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts">
export default { name: 'CreditApplicationList' }
</script>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getApplicationList, approveApplication } from '@/api/credit'
import { useTabsStore } from '@/stores/tabs'

const tabsStore = useTabsStore()
const loading = ref(false)
const tableData = ref<any[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const keyword = ref('')
const status = ref<number | undefined>(undefined)

const creditTypeMap: Record<string, string> = {
  WORKING: '流动资金', LOAN: '贷款', GUARANTEE: '担保'
}
const statusMap: Record<number, string> = { 1: '待审批', 2: '已批复', 3: '已拒绝' }
const statusType: Record<number, string> = { 1: 'warning', 2: 'success', 3: 'danger' }

// Approve dialog
const approveDialogVisible = ref(false)
const approveFormRef = ref()
const approving = ref(false)
const currentApp = ref<any>(null)
const approveForm = reactive({
  approvedAmount: 0,
  creditLimit: 0,
  startDate: '',
  endDate: '',
  remark: ''
})
const approveRules = {
  approvedAmount: [{ required: true, message: '请输入批复金额', trigger: 'blur' }],
  creditLimit: [{ required: true, message: '请输入授信额度', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
}

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res: any = await getApplicationList({
      page: page.value, size: size.value,
      keyword: keyword.value, status: status.value
    })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

function handleSearch() { page.value = 1; fetchData() }
function handleReset() { keyword.value = ''; status.value = undefined; page.value = 1; fetchData() }
function openAddTab() { tabsStore.openTab({ path: '/credit/application/add', title: '新增授信申请', icon: 'Document' }) }
function openDetailTab(id: number) { tabsStore.openTab({ path: `/credit/application/${id}`, title: `申请详情 #${id}`, icon: 'View' }) }
function openEditTab(id: number) { tabsStore.openTab({ path: `/credit/application/${id}/edit`, title: `编辑申请 #${id}`, icon: 'Edit' }) }

function openApproveDialog(row: any) {
  currentApp.value = row
  approveForm.approvedAmount = row.creditAmount
  approveForm.creditLimit = row.creditAmount
  approveForm.startDate = ''
  approveForm.endDate = ''
  approveForm.remark = ''
  approveDialogVisible.value = true
}

async function handleApprove() {
  const valid = await approveFormRef.value?.validate().catch(() => false)
  if (!valid) return
  approving.value = true
  try {
    await approveApplication(currentApp.value.id, approveForm)
    ElMessage.success('审批成功')
    approveDialogVisible.value = false
    fetchData()
  } finally { approving.value = false }
}
</script>

<style scoped>
.credit-app-list { animation: fadeSlideIn 0.35s ease-out; }
.search-card { padding: 16px; }
.search-bar { display: flex; align-items: center; gap: 10px; }
.table-card { padding: 0; overflow: hidden; }
.pagination-wrap { display: flex; justify-content: flex-end; padding: 12px 16px; }
.form-text { color: var(--text-secondary); }
</style>
