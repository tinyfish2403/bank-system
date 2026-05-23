<template>
  <div class="credit-app-form">
    <div class="glass-card form-card">
      <div class="form-header">
        <span class="form-title">{{ isEdit ? '编辑授信申请' : '新增授信申请' }}</span>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="form-body">
        <el-form-item label="客户" prop="customerId">
          <el-select v-model="form.customerId" placeholder="请选择客户" filterable style="width: 100%" :disabled="isEdit">
            <el-option v-for="c in customers" :key="c.id" :label="`${c.name} (${c.idCard})`" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="授信金额" prop="creditAmount">
          <el-input-number v-model="form.creditAmount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="授信类型" prop="creditType">
          <el-select v-model="form.creditType" placeholder="请选择授信类型" style="width: 100%">
            <el-option label="流动资金" value="WORKING" />
            <el-option label="贷款" value="LOAN" />
            <el-option label="担保" value="GUARANTEE" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请用途" prop="purpose">
          <el-input v-model="form.purpose" type="textarea" :rows="3" placeholder="请输入申请用途" />
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
export default { name: 'CreditApplicationForm' }
</script>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getApplicationById, createApplication, updateApplication } from '@/api/credit'
import { getCustomerList } from '@/api/customer'
import { useTabsStore } from '@/stores/tabs'

const route = useRoute()
const tabsStore = useTabsStore()
const formRef = ref()
const submitting = ref(false)
const customers = ref<any[]>([])

const isEdit = computed(() => !!route.params.id && route.path.includes('edit'))

const form = reactive({
  customerId: null as number | null,
  creditAmount: 0,
  creditType: '',
  purpose: ''
})

const rules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  creditAmount: [{ required: true, message: '请输入授信金额', trigger: 'blur' }],
  creditType: [{ required: true, message: '请选择授信类型', trigger: 'change' }]
}

onMounted(async () => {
  const res: any = await getCustomerList({ page: 1, size: 1000 })
  customers.value = res.data.records || []

  if (isEdit.value) {
    const id = Number(route.params.id)
    const detail: any = await getApplicationById(id)
    Object.assign(form, {
      customerId: detail.data.customerId,
      creditAmount: detail.data.creditAmount,
      creditType: detail.data.creditType,
      purpose: detail.data.purpose || ''
    })
  }
})

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateApplication(Number(route.params.id), form)
      ElMessage.success('更新成功')
    } else {
      await createApplication(form)
      ElMessage.success('新增成功')
    }
    tabsStore.closeTab(route.path)
    tabsStore.openTab({ path: '/credit/application/list', title: '授信申请', icon: 'Document' })
  } finally { submitting.value = false }
}

function handleCancel() {
  tabsStore.closeTab(route.path)
  tabsStore.openTab({ path: '/credit/application/list', title: '授信申请', icon: 'Document' })
}
</script>

<style scoped>
.credit-app-form { animation: fadeSlideIn 0.35s ease-out; }
.form-card { padding: 24px; max-width: 680px; }
.form-header { margin-bottom: 24px; }
.form-title { font-size: 18px; font-weight: 600; color: var(--text-primary); }
.form-body { max-width: 520px; }
</style>
