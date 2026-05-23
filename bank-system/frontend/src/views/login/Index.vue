<template>
  <div class="login-container">
    <!-- Background layers -->
    <div class="bg-layer bg-gradient"></div>
    <div class="bg-layer bg-grid"></div>
    <div class="bg-layer bg-nodes"></div>
    <div class="bg-layer bg-scanline"></div>
    <div class="bg-layer bg-orb"></div>

    <!-- Decorative fintech lines -->
    <div class="deco-line deco-line-h1"></div>
    <div class="deco-line deco-line-h2"></div>
    <div class="deco-line deco-line-v1"></div>
    <div class="deco-line deco-line-v2"></div>

    <div class="login-card glass-card">
      <div class="login-header">
        <div class="logo-hex">
          <svg viewBox="0 0 48 48" fill="none" class="logo-svg">
            <path d="M24 4L6 14v20l18 10 18-10V14L24 4z" stroke="var(--neon-cyan)" stroke-width="1.5" fill="none" />
            <path d="M24 14l-10 5.7v11.6L24 37l10-5.7V19.7L24 14z" stroke="var(--neon-cyan)" stroke-width="1" fill="none" opacity="0.7" />
            <circle cx="24" cy="22" r="3" fill="var(--neon-cyan)" opacity="0.8" />
            <line x1="24" y1="25" x2="24" y2="32" stroke="var(--neon-cyan)" stroke-width="1" opacity="0.5" />
            <line x1="18" y1="30" x2="30" y2="30" stroke="var(--neon-cyan)" stroke-width="0.8" opacity="0.4" />
          </svg>
        </div>
        <h2 class="login-title">NovaBank</h2>
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
          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">
            <span v-if="!loading">登 录</span>
          </el-button>
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
/* =============================================
   Login Container — Sci-Fi Fintech Theme
   ============================================= */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #060918;
  position: relative;
  overflow: hidden;
}

/* --- Background Gradient --- */
.bg-gradient {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse 80% 60% at 50% 50%, rgba(0, 212, 255, 0.06) 0%, transparent 70%),
    radial-gradient(ellipse 60% 80% at 20% 20%, rgba(168, 85, 247, 0.04) 0%, transparent 70%),
    radial-gradient(ellipse 50% 50% at 80% 80%, rgba(59, 130, 246, 0.04) 0%, transparent 70%);
}

/* --- Fintech Grid Pattern --- */
.bg-grid {
  position: absolute;
  inset: -20%;
  background-image:
    linear-gradient(rgba(0, 212, 255, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 212, 255, 0.04) 1px, transparent 1px);
  background-size: 60px 60px;
  mask-image: radial-gradient(ellipse 60% 50% at 50% 50%, black 30%, transparent 70%);
  -webkit-mask-image: radial-gradient(ellipse 60% 50% at 50% 50%, black 30%, transparent 70%);
}

/* --- Glowing Grid Nodes --- */
.bg-nodes {
  position: absolute;
  inset: -10%;
  background-image:
    radial-gradient(2px 2px at 20% 30%, rgba(0, 212, 255, 0.7), transparent),
    radial-gradient(1.5px 1.5px at 40% 60%, rgba(168, 85, 247, 0.6), transparent),
    radial-gradient(2px 2px at 60% 20%, rgba(0, 212, 255, 0.7), transparent),
    radial-gradient(1.5px 1.5px at 80% 50%, rgba(59, 130, 246, 0.5), transparent),
    radial-gradient(2px 2px at 15% 70%, rgba(0, 212, 255, 0.6), transparent),
    radial-gradient(1.5px 1.5px at 70% 80%, rgba(168, 85, 247, 0.5), transparent),
    radial-gradient(2px 2px at 50% 40%, rgba(59, 130, 246, 0.6), transparent),
    radial-gradient(1px 1px at 90% 10%, rgba(0, 212, 255, 0.5), transparent),
    radial-gradient(1px 1px at 10% 90%, rgba(168, 85, 247, 0.5), transparent),
    radial-gradient(1.5px 1.5px at 55% 75%, rgba(0, 212, 255, 0.5), transparent),
    radial-gradient(1px 1px at 35% 15%, rgba(59, 130, 246, 0.4), transparent),
    radial-gradient(1px 1px at 75% 65%, rgba(0, 212, 255, 0.4), transparent);
  background-size: 100% 100%;
  animation: nodesPulse 4s ease-in-out infinite;
}

@keyframes nodesPulse {
  0%, 100% { opacity: 0.7; }
  50% { opacity: 1; }
}

/* --- Scanline Overlay --- */
.bg-scanline {
  position: absolute;
  inset: 0;
  background: repeating-linear-gradient(
    0deg,
    transparent,
    transparent 2px,
    rgba(0, 212, 255, 0.006) 2px,
    rgba(0, 212, 255, 0.006) 4px
  );
  pointer-events: none;
}

/* --- Ambient Glow Orb --- */
.bg-orb {
  position: absolute;
  width: 600px;
  height: 600px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(0, 212, 255, 0.06) 0%, transparent 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: orbBreath 8s ease-in-out infinite;
  pointer-events: none;
}

@keyframes orbBreath {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.6; }
  50% { transform: translate(-50%, -50%) scale(1.15); opacity: 1; }
}

/* --- Decorative Data Flow Lines --- */
.deco-line {
  position: absolute;
  pointer-events: none;
  z-index: 0;
}
.deco-line::after {
  content: '';
  position: absolute;
  width: 80px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(0, 212, 255, 0.3), transparent);
}

.deco-line-h1 {
  top: 18%;
  left: 10%;
  width: 200px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(0, 212, 255, 0.15), transparent);
  animation: dataFlow1 6s ease-in-out infinite;
}
.deco-line-h2 {
  top: 78%;
  right: 12%;
  width: 180px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(168, 85, 247, 0.12), transparent);
  animation: dataFlow2 7s ease-in-out infinite;
}
.deco-line-v1 {
  top: 15%;
  right: 22%;
  width: 1px;
  height: 150px;
  background: linear-gradient(0deg, transparent, rgba(0, 212, 255, 0.12), transparent);
  animation: dataFlow3 8s ease-in-out infinite;
}
.deco-line-v2 {
  bottom: 15%;
  left: 25%;
  width: 1px;
  height: 120px;
  background: linear-gradient(0deg, transparent, rgba(59, 130, 246, 0.1), transparent);
  animation: dataFlow4 5s ease-in-out infinite;
}

@keyframes dataFlow1 {
  0%, 100% { opacity: 0.3; transform: translateX(0); }
  50% { opacity: 0.8; transform: translateX(20px); }
}
@keyframes dataFlow2 {
  0%, 100% { opacity: 0.2; transform: translateX(0); }
  50% { opacity: 0.7; transform: translateX(-15px); }
}
@keyframes dataFlow3 {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.7; }
}
@keyframes dataFlow4 {
  0%, 100% { opacity: 0.2; }
  50% { opacity: 0.6; }
}

/* =============================================
   Login Card
   ============================================= */
.login-card {
  position: relative;
  z-index: 2;
  width: 420px;
  padding: 48px 44px 40px;
  animation: cardEntry 0.8s cubic-bezier(0.22, 0.61, 0.36, 1);
  background: var(--bg-glass) !important;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--glass-border) !important;
  border-radius: var(--radius-lg) !important;
  box-shadow:
    0 4px 48px rgba(0, 0, 0, 0.5),
    0 0 0 1px rgba(0, 212, 255, 0.06) inset,
    var(--glass-shadow);
}

@keyframes cardEntry {
  from { opacity: 0; transform: translateY(24px) scale(0.97); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

/* --- Hexagon Logo --- */
.logo-hex {
  width: 56px;
  height: 56px;
  margin: 0 auto 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  background: rgba(0, 212, 255, 0.06);
  border: 1px solid rgba(0, 212, 255, 0.15);
  box-shadow: 0 0 24px rgba(0, 212, 255, 0.1);
}

.logo-svg {
  width: 36px;
  height: 36px;
  filter: drop-shadow(0 0 6px rgba(0, 212, 255, 0.3));
  animation: logoFloat 3s ease-in-out infinite;
}

@keyframes logoFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-2px); }
}

.login-title {
  margin: 0;
  font-family: 'Fira Code', 'Courier New', monospace;
  font-size: 30px;
  font-weight: 700;
  letter-spacing: 4px;
  background: linear-gradient(135deg, var(--neon-cyan) 0%, var(--neon-purple) 50%, var(--neon-cyan) 100%);
  background-size: 200% 200%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: titleShimmer 4s ease-in-out infinite;
}

@keyframes titleShimmer {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.login-subtitle {
  margin: 8px 0 0;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-muted);
  letter-spacing: 3px;
  text-transform: uppercase;
}

/* =============================================
   Form Styling
   ============================================= */
.login-card :deep(.el-form-item) {
  margin-bottom: 20px;
}

.login-card :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.03) !important;
  border: 1px solid rgba(0, 212, 255, 0.12) !important;
  border-radius: 8px !important;
  box-shadow: none !important;
  transition: all 0.3s ease;
  padding-left: 12px;
}
.login-card :deep(.el-input__wrapper:hover) {
  border-color: rgba(0, 212, 255, 0.25) !important;
  background: rgba(255, 255, 255, 0.05) !important;
}
.login-card :deep(.el-input.is-focus .el-input__wrapper) {
  border-color: var(--neon-cyan) !important;
  box-shadow: 0 0 12px rgba(0, 212, 255, 0.1), 0 0 0 1px rgba(0, 212, 255, 0.15) !important;
  background: rgba(255, 255, 255, 0.04) !important;
}
.login-card :deep(.el-input__inner) {
  color: var(--text-primary) !important;
  font-family: 'Fira Sans', 'PingFang SC', sans-serif !important;
}
.login-card :deep(.el-input__prefix .el-icon) {
  color: rgba(0, 212, 255, 0.4);
  transition: color 0.3s;
}
.login-card :deep(.el-input.is-focus .el-input__prefix .el-icon) {
  color: var(--neon-cyan);
}

/* --- Login Button --- */
.login-btn {
  width: 100%;
  height: 46px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 6px;
  border-radius: 8px !important;
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.8), rgba(0, 190, 240, 0.7)) !important;
  border: 1px solid rgba(0, 212, 255, 0.3) !important;
  color: #fff !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  position: relative;
  overflow: hidden;
}

.login-btn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, transparent 40%, rgba(255,255,255,0.1) 50%, transparent 60%);
  transform: translateX(-100%);
  transition: transform 0.6s;
}

.login-btn:hover {
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.4), 0 0 40px rgba(0, 212, 255, 0.15) !important;
  transform: translateY(-1px);
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.9), rgba(0, 210, 255, 0.8)) !important;
}
.login-btn:hover::before {
  transform: translateX(100%);
}
.login-btn:active {
  transform: translateY(0);
}
</style>
