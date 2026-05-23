<template>
  <div class="tab-bar">
    <div class="tab-bar-inner" ref="tabBarRef" @wheel.prevent="handleWheel">
      <div
        v-for="tab in tabsStore.tabs"
        :key="tab.path"
        class="tab-item"
        :class="{ active: tabsStore.activeTab === tab.path }"
        @click="tabsStore.setActiveTab(tab.path)"
        @auxclick.middle.prevent="tabsStore.closeTab(tab.path)"
        @contextmenu.prevent="showMenu($event, tab)"
      >
        <el-icon v-if="tab.icon" class="tab-icon"><component :is="tab.icon" /></el-icon>
        <span class="tab-title">{{ tab.title }}</span>
        <span
          v-if="!tab.affix"
          class="tab-close"
          @click.stop="tabsStore.closeTab(tab.path)"
        >
          <el-icon :size="12"><Close /></el-icon>
        </span>
      </div>
    </div>

    <!-- Right-click context menu -->
    <teleport to="body">
      <div
        v-if="contextMenu.visible"
        class="tab-context-menu glass-card-strong"
        :style="{ left: contextMenu.x + 'px', top: contextMenu.y + 'px' }"
      >
        <div class="context-item" @click="handleContextAction('refresh')">
          <el-icon :size="14"><Refresh /></el-icon> 刷新
        </div>
        <div class="context-item" @click="handleContextAction('close')" v-if="!contextMenu.tab?.affix">
          <el-icon :size="14"><Close /></el-icon> 关闭
        </div>
        <div class="context-item" @click="handleContextAction('closeOthers')">
          <el-icon :size="14"><Minus /></el-icon> 关闭其他
        </div>
        <div class="context-item" @click="handleContextAction('closeLeft')">
          <el-icon :size="14"><ArrowLeft /></el-icon> 关闭左侧
        </div>
        <div class="context-item" @click="handleContextAction('closeRight')">
          <el-icon :size="14"><ArrowRight /></el-icon> 关闭右侧
        </div>
        <div class="context-item" @click="handleContextAction('closeAll')">
          <el-icon :size="14"><CircleClose /></el-icon> 关闭所有
        </div>
      </div>
    </teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useTabsStore, type TabItem } from '@/stores/tabs'

const tabsStore = useTabsStore()
const tabBarRef = ref<HTMLElement>()

const contextMenu = reactive({
  visible: false,
  x: 0,
  y: 0,
  tab: null as TabItem | null
})

function showMenu(e: MouseEvent, tab: TabItem) {
  contextMenu.visible = true
  contextMenu.x = e.clientX
  contextMenu.y = e.clientY
  contextMenu.tab = tab
}

function hideMenu() {
  contextMenu.visible = false
  contextMenu.tab = null
}

function handleContextAction(action: string) {
  const path = contextMenu.tab?.path
  if (!path) return
  switch (action) {
    case 'refresh':
      tabsStore.setActiveTab(path)
      break
    case 'close':
      tabsStore.closeTab(path)
      break
    case 'closeOthers':
      tabsStore.closeOtherTabs(path)
      break
    case 'closeLeft':
      tabsStore.closeLeftTabs(path)
      break
    case 'closeRight':
      tabsStore.closeRightTabs(path)
      break
    case 'closeAll':
      tabsStore.closeAllTabs()
      break
  }
  hideMenu()
}

function handleWheel(e: WheelEvent) {
  if (tabBarRef.value) {
    tabBarRef.value.scrollLeft += e.deltaY
  }
}

onMounted(() => document.addEventListener('click', hideMenu))
onUnmounted(() => document.removeEventListener('click', hideMenu))
</script>

<style scoped>
.tab-bar {
  height: 38px;
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: stretch;
  flex-shrink: 0;
}
.tab-bar-inner {
  display: flex;
  overflow-x: auto;
  overflow-y: hidden;
  scrollbar-width: none;
  flex: 1;
  padding: 0 4px;
}
.tab-bar-inner::-webkit-scrollbar {
  display: none;
}
.tab-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 0 14px;
  height: 34px;
  margin: 2px 1px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  color: var(--text-secondary);
  background: transparent;
  border: 1px solid transparent;
  white-space: nowrap;
  transition: all 0.2s ease;
  user-select: none;
  flex-shrink: 0;
  position: relative;
}
.tab-item:hover {
  background: rgba(0, 212, 255, 0.06);
  color: var(--text-primary);
}
.tab-item.active {
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.12), rgba(0, 212, 255, 0.04));
  border-color: rgba(0, 212, 255, 0.2);
  color: var(--neon-cyan);
}
.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 8px;
  right: 8px;
  height: 2px;
  background: var(--neon-cyan);
  border-radius: 1px;
  box-shadow: 0 0 6px rgba(0, 212, 255, 0.5);
}
.tab-icon {
  font-size: 14px;
}
.tab-close {
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 3px;
  margin-left: 2px;
  opacity: 0;
  transition: all 0.2s;
}
.tab-item:hover .tab-close {
  opacity: 0.7;
}
.tab-close:hover {
  opacity: 1 !important;
  background: rgba(239, 68, 68, 0.2);
  color: var(--neon-red);
}

.tab-context-menu {
  position: fixed;
  z-index: 10000;
  min-width: 140px;
  padding: 6px 0;
  border-radius: var(--radius-sm);
}
.context-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 7px 16px;
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.15s;
}
.context-item:hover {
  background: rgba(0, 212, 255, 0.08);
  color: var(--neon-cyan);
}
</style>
