<template>
  <div class="login-container">
    <div class="login-card glass-card">
      <div class="login-header">
        <span class="login-icon">&#9670;</span>
        <h2 class="login-title text-gradient">NovaBank</h2>
        <p class="login-subtitle">Admin Console</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" size="large">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
export default { name: 'Login' }
</script>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: 'admin',
  password: '123456'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await authStore.doLogin(form.username, form.password)
    await authStore.fetchUserInfo()
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch {
    ElMessage.error('用户名或密码错误')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: var(--bg-primary);
  position: relative;
  overflow: hidden;
}

/* Starfield overlay */
.login-container::before {
  content: '';
  position: absolute;
  inset: -50%;
  background-image:
    radial-gradient(1px 1px at 15% 25%, rgba(0,212,255,0.5), transparent),
    radial-gradient(1px 1px at 35% 55%, rgba(168,85,247,0.4), transparent),
    radial-gradient(2px 2px at 55% 15%, rgba(0,212,255,0.5), transparent),
    radial-gradient(1px 1px at 75% 65%, rgba(255,255,255,0.3), transparent),
    radial-gradient(2px 2px at 20% 80%, rgba(59,130,246,0.4), transparent),
    radial-gradient(1px 1px at 85% 20%, rgba(0,212,255,0.4), transparent),
    radial-gradient(1px 1px at 60% 70%, rgba(168,85,247,0.35), transparent),
    radial-gradient(2px 2px at 40% 35%, rgba(0,212,255,0.3), transparent);
  background-size: 150% 150%;
  animation: starDrift 60s linear infinite;
}

/* Scan line */
.login-container::after {
  content: '';
  position: absolute;
  inset: 0;
  background: repeating-linear-gradient(
    0deg,
    transparent,
    transparent 2px,
    rgba(0, 212, 255, 0.008) 2px,
    rgba(0, 212, 255, 0.008) 4px
  );
  pointer-events: none;
}

.login-card {
  position: relative;
  z-index: 1;
  width: 400px;
  padding: 44px 40px;
  animation: fadeSlideIn 0.6s ease-out;
}
.login-header {
  text-align: center;
  margin-bottom: 32px;
}
.login-icon {
  font-size: 40px;
  color: var(--neon-cyan);
  text-shadow: 0 0 20px rgba(0, 212, 255, 0.5);
  display: block;
  margin-bottom: 8px;
}
.login-title {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 3px;
  background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.login-subtitle {
  margin: 6px 0 0;
  font-size: 13px;
  color: var(--text-muted);
  letter-spacing: 2px;
  text-transform: uppercase;
}
.login-btn {
  width: 100%;
  height: 44px;
  font-size: 15px;
  letter-spacing: 4px;
  border-radius: var(--radius-sm);
  transition: all 0.3s ease;
}
.login-btn:hover {
  box-shadow: var(--glow-cyan);
  transform: translateY(-1px);
}
</style>
