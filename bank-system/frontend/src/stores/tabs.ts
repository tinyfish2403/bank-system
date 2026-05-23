import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import router from '@/router'

export interface TabItem {
  path: string
  title: string
  icon?: string
  affix?: boolean
}

export const useTabsStore = defineStore('tabs', () => {
  const tabs = ref<TabItem[]>([
    { path: '/dashboard', title: '仪表盘', icon: 'HomeFilled', affix: true }
  ])
  const activeTab = ref<string>('/dashboard')

  const cachedViewNames = computed(() =>
    tabs.value.map(t => {
      // Map tab path to route name for keep-alive include
      const resolved = router.resolve(t.path)
      return resolved?.name as string || ''
    }).filter(Boolean)
  )

  function openTab(tab: Omit<TabItem, 'affix'> & { affix?: boolean }) {
    const existing = tabs.value.find(t => t.path === tab.path)
    if (existing) {
      activeTab.value = existing.path
      router.push(existing.path)
      return
    }
    tabs.value.push({ ...tab, affix: tab.affix || false })
    activeTab.value = tab.path
    router.push(tab.path)
  }

  function closeTab(path: string) {
    const tab = tabs.value.find(t => t.path === path)
    if (!tab || tab.affix) return

    const index = tabs.value.indexOf(tab)
    tabs.value.splice(index, 1)

    if (activeTab.value === path) {
      // Activate adjacent tab
      const next = tabs.value[Math.min(index, tabs.value.length - 1)]
      if (next) {
        activeTab.value = next.path
        router.push(next.path)
      }
    }
  }

  function closeOtherTabs(path: string) {
    tabs.value = tabs.value.filter(t => t.affix || t.path === path)
    activeTab.value = path
    router.push(path)
  }

  function closeAllTabs() {
    tabs.value = tabs.value.filter(t => t.affix)
    const first = tabs.value[0]
    if (first) {
      activeTab.value = first.path
      router.push(first.path)
    }
  }

  function closeLeftTabs(path: string) {
    const index = tabs.value.findIndex(t => t.path === path)
    if (index < 0) return
    tabs.value = tabs.value.filter((t, i) => t.affix || i >= index)
    activeTab.value = path
    router.push(path)
  }

  function closeRightTabs(path: string) {
    const index = tabs.value.findIndex(t => t.path === path)
    if (index < 0) return
    tabs.value = tabs.value.filter((t, i) => t.affix || i <= index)
    activeTab.value = path
    router.push(path)
  }

  function setActiveTab(path: string) {
    activeTab.value = path
    router.push(path)
  }

  function syncFromRoute(path: string, title?: string, icon?: string) {
    if (path === '/' || path === '/login') return
    const existing = tabs.value.find(t => t.path === path)
    if (!existing) {
      const resolved = router.resolve(path)
      tabs.value.push({
        path,
        title: title || (resolved.meta.title as string) || path,
        icon,
        affix: !!resolved.meta.affix
      })
    }
    activeTab.value = path
  }

  return {
    tabs,
    activeTab,
    cachedViewNames,
    openTab,
    closeTab,
    closeOtherTabs,
    closeAllTabs,
    closeLeftTabs,
    closeRightTabs,
    setActiveTab,
    syncFromRoute
  }
})
