<template>
  <el-container class="layout-container">
    <!-- Sidebar -->
    <el-aside :width="sidebarCollapsed ? '64px' : '220px'" class="layout-aside">
      <div class="logo-area">
        <span class="logo-icon">&#9670;</span>
        <span v-show="!sidebarCollapsed" class="logo-text">NovaBank</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="sidebarCollapsed"
        @select="handleMenuSelect"
        background-color="transparent"
      >
        <template v-for="menu in visibleMenus" :key="menu.id">
          <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="String(menu.id)">
            <template #title>
              <el-icon v-if="menu.icon"><component :is="menu.icon" /></el-icon>
              <span>{{ menu.menuName }}</span>
            </template>
            <el-menu-item
              v-for="child in menu.children"
              :key="child.id"
              :index="child.path || String(child.id)"
            >
              <el-icon v-if="child.icon"><component :is="child.icon" /></el-icon>
              <span>{{ child.menuName }}</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="menu.path || String(menu.id)">
            <el-icon v-if="menu.icon"><component :is="menu.icon" /></el-icon>
            <span>{{ menu.menuName }}</span>
          </el-menu-item>
        </template>
      </el-menu>

      <div class="sidebar-footer" @click="appStore.toggleSidebar()">
        <el-icon :size="16">
          <Fold v-if="!sidebarCollapsed" />
          <Expand v-else />
        </el-icon>
      </div>
    </el-aside>

    <!-- Main area -->
    <el-container class="main-container">
      <!-- Header -->
      <el-header class="layout-header">
        <div class="header-left">
          <Breadcrumb />
        </div>
        <div class="header-right">
          <el-dropdown trigger="click">
            <span class="user-info">
              <span class="user-avatar">{{ (authStore.userInfo?.nickname || 'U')[0] }}</span>
              <span class="user-name">{{ authStore.userInfo?.nickname || authStore.userInfo?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- Tab Bar -->
      <TabBar />

      <!-- Content -->
      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <keep-alive :include="tabsStore.cachedViewNames">
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore, type MenuItem } from '@/stores/auth'
import { useAppStore } from '@/stores/app'
import { useTabsStore } from '@/stores/tabs'
import Breadcrumb from './Breadcrumb.vue'
import TabBar from './TabBar.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const appStore = useAppStore()
const tabsStore = useTabsStore()

const sidebarCollapsed = computed(() => appStore.sidebarCollapsed)
const activeMenu = computed(() => tabsStore.activeTab)

// Filter out hidden menus
function filterHidden(menus: MenuItem[]): MenuItem[] {
  return menus
    .map(m => ({ ...m, children: m.children ? filterHidden(m.children) : [] }))
    .filter(m => {
      // For leaf menus, check if route has hidden:true
      if (m.path && (!m.children || m.children.length === 0)) {
        const resolved = router.resolve(m.path)
        if (resolved?.meta?.hidden) return false
      }
      // For parent menus, check if any children remain
      if (!m.path && m.children.length === 0) return false
      return true
    })
}

const visibleMenus = computed(() => filterHidden(authStore.menus))

function findMenuByPath(menus: MenuItem[], path: string): { title: string; icon?: string } | null {
  for (const m of menus) {
    if (m.path === path) return { title: m.menuName, icon: m.icon || undefined }
    if (m.children) {
      const found = findMenuByPath(m.children, path)
      if (found) return found
    }
  }
  return null
}

function handleMenuSelect(index: string) {
  // Skip non-path indices (like directory IDs)
  if (!index.startsWith('/')) return
  const info = findMenuByPath(authStore.menus, index)
  tabsStore.openTab({
    path: index,
    title: info?.title || index,
    icon: info?.icon
  })
}

function handleLogout() {
  authStore.doLogout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  background: var(--bg-primary);
}

/* === Sidebar === */
.layout-aside {
  background: var(--bg-secondary);
  border-right: 1px solid var(--border-color);
  box-shadow: 2px 0 30px rgba(0, 212, 255, 0.04);
  transition: width 0.3s ease;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.logo-area {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-bottom: 1px solid var(--border-color);
  color: var(--neon-cyan);
  flex-shrink: 0;
}
.logo-icon {
  font-size: 22px;
  text-shadow: 0 0 12px rgba(0, 212, 255, 0.5);
}
.logo-text {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 2px;
  text-shadow: 0 0 10px rgba(0, 212, 255, 0.4);
  white-space: nowrap;
}

.layout-aside :deep(.el-menu) {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 8px 0;
}

.sidebar-footer {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-top: 1px solid var(--border-color);
  cursor: pointer;
  color: var(--text-muted);
  transition: all 0.3s;
  flex-shrink: 0;
}
.sidebar-footer:hover {
  color: var(--neon-cyan);
  text-shadow: 0 0 8px rgba(0, 212, 255, 0.4);
}

/* === Main Container === */
.main-container {
  flex-direction: column;
  min-width: 0;
}

/* === Header === */
.layout-header {
  background: var(--bg-glass-strong);
  backdrop-filter: blur(var(--glass-blur));
  -webkit-backdrop-filter: blur(var(--glass-blur));
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 48px;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.header-right {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  border-radius: var(--radius-sm);
  transition: background 0.2s;
}
.user-info:hover {
  background: rgba(0, 212, 255, 0.06);
}
.user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
}
.user-name {
  color: var(--text-secondary);
  font-size: 13px;
}

/* === Content === */
.layout-main {
  background: transparent;
  padding: 16px 20px;
  overflow-y: auto;
  flex: 1;
}
</style>
