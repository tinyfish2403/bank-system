<template>
  <div class="placeholder-page">
    <div class="glass-card" style="padding: 40px; text-align: center">
      <h3 style="margin-top: 0; color: var(--text-primary)">用户管理</h3>
      <el-empty description="预置管理员账号，暂不提供前端管理界面" />
      <el-button type="primary" @click="goDashboard">
        <el-icon><HomeFilled /></el-icon> 返回首页
      </el-button>
    </div>
  </div>
</template>

<script lang="ts">
export default { name: 'SystemUser' }
</script>

<script setup lang="ts">
import { useTabsStore } from '@/stores/tabs'
const tabsStore = useTabsStore()
function goDashboard() {
  tabsStore.closeTab('/system/user')
  tabsStore.setActiveTab('/dashboard')
}
</script>

<style scoped>
.placeholder-page {
  animation: fadeSlideIn 0.35s ease-out;
  max-width: 600px;
}
</style>
