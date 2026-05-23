<template>
  <div class="customer-form">
    <div class="glass-card form-card">
      <div class="form-header">
        <span class="form-title">{{ isEdit ? '编辑客户' : '新增客户' }}</span>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" class="form-body">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入18位身份证号" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
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
export default { name: 'CustomerForm' }
</script>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCustomerById, createCustomer, updateCustomer } from '@/api/customer'
import { useTabsStore } from '@/stores/tabs'

const route = useRoute()
const tabsStore = useTabsStore()
const formRef = ref()
const submitting = ref(false)

const isEdit = computed(() => !!route.params.id && route.path.includes('edit'))

const form = reactive({
  name: '',
  idCard: '',
  phone: '',
  email: '',
  address: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }, { min: 2, max: 50, message: '姓名长度为2-50个字符', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }, { len: 18, message: '身份证号必须为18位', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
}

onMounted(async () => {
  if (isEdit.value) {
    const id = Number(route.params.id)
    const res: any = await getCustomerById(id)
    Object.assign(form, res.data)
  }
})

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateCustomer(Number(route.params.id), form)
      ElMessage.success('更新成功')
      // Close the edit tab
      tabsStore.closeTab(route.path)
    } else {
      await createCustomer(form)
      ElMessage.success('新增成功')
      tabsStore.closeTab(route.path)
    }
    // Switch to list tab
    tabsStore.openTab({ path: '/customer/list', title: '客户列表', icon: 'List' })
  } finally {
    submitting.value = false
  }
}

function handleCancel() {
  tabsStore.closeTab(route.path)
  tabsStore.openTab({ path: '/customer/list', title: '客户列表', icon: 'List' })
}
</script>

<style scoped>
.customer-form {
  animation: fadeSlideIn 0.35s ease-out;
}
.form-card {
  padding: 24px;
  max-width: 680px;
}
.form-header {
  margin-bottom: 24px;
}
.form-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}
.form-body {
  max-width: 520px;
}
</style>
