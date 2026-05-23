<template>
  <div class="dashboard">
    <h2 class="welcome-title">
      欢迎回来，<span class="text-gradient">{{ authStore.userInfo?.nickname || '用户' }}</span>
    </h2>

    <el-row :gutter="16" class="stat-row">
      <el-col :span="6" v-for="(stat, i) in stats" :key="i">
        <div class="stat-card glass-card pulse-border" :style="{ animationDelay: i * 0.3 + 's' }">
          <div class="stat-icon" :style="{ background: stat.color }">
            <el-icon :size="22"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">{{ stat.label }}</div>
            <div class="stat-value text-gradient">{{ stat.value }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="16">
        <div class="glass-card" style="padding: 20px">
          <h3 class="card-title">系统信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户名">{{ authStore.userInfo?.username }}</el-descriptions-item>
            <el-descriptions-item label="角色">{{ authStore.userInfo?.roles?.join(', ') }}</el-descriptions-item>
            <el-descriptions-item label="系统版本">1.0.0</el-descriptions-item>
            <el-descriptions-item label="前端框架">Vue 3 + Element Plus</el-descriptions-item>
            <el-descriptions-item label="后端框架">Spring Boot 3.2</el-descriptions-item>
            <el-descriptions-item label="数据库">MySQL 8.0</el-descriptions-item>
          </el-descriptions>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="glass-card quick-actions" style="padding: 20px">
          <h3 class="card-title">快捷操作</h3>
          <el-button class="action-btn" @click="$router.push('/customer/list')">
            <el-icon><User /></el-icon> 客户管理
          </el-button>
          <el-button class="action-btn" @click="$router.push('/customer/add')">
            <el-icon><Plus /></el-icon> 新增客户
          </el-button>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts">
export default { name: 'Dashboard' }
</script>

<script setup lang="ts">
import { reactive } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const stats = reactive([
  { color: 'var(--neon-blue)', icon: 'User', label: '客户总数', value: 0 },
  { color: 'var(--neon-green)', icon: 'CreditCard', label: '账户总数', value: 0 },
  { color: 'var(--neon-orange)', icon: 'Money', label: '交易笔数', value: 0 },
  { color: 'var(--neon-red)', icon: 'BankCard', label: '贷款笔数', value: 0 }
])
</script>

<style scoped>
.dashboard {
  animation: fadeSlideIn 0.35s ease-out;
}
.welcome-title {
  margin: 0 0 20px;
  font-family: 'Fira Code', 'Courier New', monospace;
  font-size: 22px;
  font-weight: 600;
  color: var(--text-primary);
}
.stat-row {
  margin-top: 0;
}
.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}
.stat-card:hover {
  transform: translateY(-3px);
  border-color: var(--neon-cyan) !important;
}
.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.3);
}
.stat-info {
  flex: 1;
}
.stat-label {
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 4px;
  letter-spacing: 1px;
}
.stat-value {
  font-family: 'Fira Code', 'Courier New', monospace;
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.card-title {
  margin: 0 0 16px;
  font-family: 'Fira Code', 'Courier New', monospace;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  letter-spacing: 1px;
}
.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.action-btn {
  width: 100%;
  justify-content: flex-start;
  gap: 8px;
  border-radius: var(--radius-sm);
  height: 40px;
}
</style>
