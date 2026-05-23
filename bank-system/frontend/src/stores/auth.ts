import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getUserInfo } from '@/api/auth'
import { setToken, removeToken } from '@/utils/auth'
import type { RouteRecordRaw } from 'vue-router'

export interface MenuItem {
  id: number
  parentId: number
  menuName: string
  path: string | null
  component: string | null
  icon: string | null
  menuType: number
  children: MenuItem[]
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(null)
  const userInfo = ref<any>(null)
  const menus = ref<MenuItem[]>([])

  async function doLogin(username: string, password: string) {
    const res: any = await loginApi({ username, password })
    token.value = res.data.token
    setToken(res.data.token)
  }

  async function fetchUserInfo() {
    const res: any = await getUserInfo()
    userInfo.value = res.data
    menus.value = res.data.menus || []
  }

  function doLogout() {
    token.value = null
    userInfo.value = null
    menus.value = []
    removeToken()
  }

  function generateRoutes(menuList: MenuItem[]): RouteRecordRaw[] {
    const routes: RouteRecordRaw[] = []
    for (const menu of menuList) {
      if (menu.path && menu.component) {
        routes.push({
          path: menu.path,
          name: menu.menuName,
          component: () => import(`@/views/${menu.component}`),
          meta: { title: menu.menuName, icon: menu.icon }
        })
      }
      if (menu.children && menu.children.length > 0) {
        routes.push(...generateRoutes(menu.children))
      }
    }
    return routes
  }

  return { token, userInfo, menus, doLogin, fetchUserInfo, doLogout, generateRoutes }
})
