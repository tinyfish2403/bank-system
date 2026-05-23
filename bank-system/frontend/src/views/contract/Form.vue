<template>
  <div class="contract-form">
    <div class="glass-card form-card">
      <div class="form-header">
        <span class="form-title">{{ isEdit ? '合同变更' : '签订合同' }}</span>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="form-body">
        <el-form-item v-if="!isEdit" label="关联批复" prop="approvalId">
          <el-select v-model="form.approvalId" placeholder="选择有效批复" filterable style="width: 100%" @change="onApprovalChange">
            <el-option v-for="a in approvals" :key="a.id" :label="`${a.approvalNo} (${a.customerName}, 额度: ${a.creditLimit})`" :value="a.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户">{{ customerName || '选择批复后自动带入' }}</el-form-item>
        <el-form-item label="合同金额" prop="contractAmount">
          <el-input-number v-model="form.contractAmount" :min="0" :precision="2" style="width: 100%" />
          <span v-if="approvalLimit" class="form-hint">批复额度: {{ approvalLimit.toLocaleString() }}</span>
        </el-form-item>
        <el-form-item label="合同类型" prop="contractType">
          <el-select v-model="form.contractType" placeholder="请选择合同类型" style="width: 100%">
            <el-option label="授信合同" value="CREDIT" />
            <el-option label="贷款合同" value="LOAN" />
            <el-option label="担保合同" value="GUARANTEE" />
          </el-select>
        </el-form-item>
        <el-form-item label="签订日期" prop="signDate">
          <el-date-picker v-model="form.signDate" type="date" placeholder="选择签订日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="备注信息" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            <el-icon><Check /></el-icon> 保存
          </el-button>
          <el-button @click="handleCancel">
            <el-icon><Close /></el-icon> 取消
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
export default { name: 'ContractForm' }
</script>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getContractById, createContract, updateContract } from '@/api/contract'
import { getApprovalList } from '@/api/credit'
import { useTabsStore } from '@/stores/tabs'

const route = useRoute()
const tabsStore = useTabsStore()
const formRef = ref()
const submitting = ref(false)
const approvals = ref<any[]>([])
const approvalLimit = ref(0)
const customerName = ref('')

const isEdit = computed(() => !!route.params.id && route.path.includes('edit'))

const form = reactive({
  approvalId: null as number | null,
  contractAmount: 0,
  contractType: '',
  signDate: '',
  remark: ''
})

const rules = {
  approvalId: [{ required: true, message: '请选择批复', trigger: 'change' }],
  contractAmount: [{ required: true, message: '请输入合同金额', trigger: 'blur' }],
  contractType: [{ required: true, message: '请选择合同类型', trigger: 'change' }],
  signDate: [{ required: true, message: '请选择签订日期', trigger: 'change' }]
}

onMounted(async () => {
  // Load valid approvals (status=1) for selection
  const res: any = await getApprovalList({ page: 1, size: 1000, status: 1 })
  approvals.value = res.data.records || []

  if (isEdit.value) {
    const id = Number(route.params.id)
    const detail: any = await getContractById(id)
    const d = detail.data
    Object.assign(form, {
      contractAmount: d.contractAmount,
      contractType: d.contractType,
      signDate: d.signDate,
      remark: d.remark || ''
    })
    approvalLimit.value = d.contractAmount
    customerName.value = d.customerName || ''
  }
})

function onApprovalChange(val: number) {
  const selected = approvals.value.find(a => a.id === val)
  if (selected) {
    approvalLimit.value = selected.creditLimit || 0
    customerName.value = selected.customerName || ''
  }
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (!isEdit.value && form.contractAmount > approvalLimit.value) {
    ElMessage.error('合同金额不能超过批复额度')
    return
  }
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateContract(Number(route.params.id), {
        contractAmount: form.contractAmount,
        contractType: form.contractType,
        remark: form.remark
      })
      ElMessage.success('变更成功')
    } else {
      await createContract(form)
      ElMessage.success('签订成功')
    }
    tabsStore.closeTab(route.path)
    tabsStore.openTab({ path: '/contract/list', title: '合同列表', icon: 'Tickets' })
  } finally { submitting.value = false }
}

function handleCancel() {
  tabsStore.closeTab(route.path)
  tabsStore.openTab({ path: '/contract/list', title: '合同列表', icon: 'Tickets' })
}
</script>

<style scoped>
.contract-form { animation: fadeSlideIn 0.35s ease-out; }
.form-card { padding: 24px; max-width: 680px; }
.form-header { margin-bottom: 24px; }
.form-title { font-size: 18px; font-weight: 600; color: var(--text-primary); }
.form-body { max-width: 520px; }
.form-hint { margin-left: 10px; color: var(--neon-cyan); font-size: 12px; }
</style>
