import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { getToken } from '@/utils/auth'

const staticRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Index.vue'),
    meta: { noAuth: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/layout/Index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Index.vue'),
        meta: { title: '仪表盘', icon: 'HomeFilled', affix: true }
      },
      {
        path: 'customer/list',
        name: 'CustomerList',
        component: () => import('@/views/customer/List.vue'),
        meta: { title: '客户列表', icon: 'List' }
      },
      {
        path: 'customer/add',
        name: 'CustomerAdd',
        component: () => import('@/views/customer/Form.vue'),
        meta: { title: '新增客户', icon: 'Plus' }
      },
      {
        path: 'customer/:id',
        name: 'CustomerDetail',
        component: () => import('@/views/customer/Detail.vue'),
        meta: { title: '客户详情', icon: 'View', hidden: true }
      },
      {
        path: 'customer/:id/edit',
        name: 'CustomerEdit',
        component: () => import('@/views/customer/Form.vue'),
        meta: { title: '编辑客户', icon: 'Edit', hidden: true }
      },
      {
        path: 'credit/application/list',
        name: 'CreditAppList',
        component: () => import('@/views/credit/ApplicationList.vue'),
        meta: { title: '授信申请', icon: 'Document' }
      },
      {
        path: 'credit/application/add',
        name: 'CreditAppAdd',
        component: () => import('@/views/credit/ApplicationForm.vue'),
        meta: { title: '新增授信申请', icon: 'Document', hidden: true }
      },
      {
        path: 'credit/application/:id',
        name: 'CreditAppDetail',
        component: () => import('@/views/credit/ApplicationDetail.vue'),
        meta: { title: '授信申请详情', icon: 'View', hidden: true }
      },
      {
        path: 'credit/application/:id/edit',
        name: 'CreditAppEdit',
        component: () => import('@/views/credit/ApplicationForm.vue'),
        meta: { title: '编辑授信申请', icon: 'Edit', hidden: true }
      },
      {
        path: 'credit/approval/list',
        name: 'CreditApprovalList',
        component: () => import('@/views/credit/ApprovalList.vue'),
        meta: { title: '批复查询', icon: 'Checked' }
      },
      {
        path: 'credit/approval/:id',
        name: 'CreditApprovalDetail',
        component: () => import('@/views/credit/ApprovalDetail.vue'),
        meta: { title: '批复详情', icon: 'View', hidden: true }
      },
      {
        path: 'contract/list',
        name: 'ContractList',
        component: () => import('@/views/contract/List.vue'),
        meta: { title: '合同列表', icon: 'Tickets' }
      },
      {
        path: 'contract/add',
        name: 'ContractAdd',
        component: () => import('@/views/contract/Form.vue'),
        meta: { title: '签订合同', icon: 'EditPen', hidden: true }
      },
      {
        path: 'contract/:id',
        name: 'ContractDetail',
        component: () => import('@/views/contract/Detail.vue'),
        meta: { title: '合同详情', icon: 'View', hidden: true }
      },
      {
        path: 'contract/:id/edit',
        name: 'ContractEdit',
        component: () => import('@/views/contract/Form.vue'),
        meta: { title: '合同变更', icon: 'Edit', hidden: true }
      },
      {
        path: 'system/user',
        name: 'SystemUser',
        component: () => import('@/views/system/UserList.vue'),
        meta: { title: '用户管理', icon: 'UserFilled' }
      },
      {
        path: 'system/role',
        name: 'SystemRole',
        component: () => import('@/views/system/RoleList.vue'),
        meta: { title: '角色管理', icon: 'Avatar' }
      },
      {
        path: 'system/menu',
        name: 'SystemMenu',
        component: () => import('@/views/system/MenuList.vue'),
        meta: { title: '菜单管理', icon: 'Menu' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: staticRoutes
})

// Path-to-title map for tab auto-naming
const titleMap: Record<string, string> = {}
staticRoutes
  .find(r => r.path === '/')
  ?.children?.forEach(r => {
    if (r.path && r.meta?.title) {
      titleMap['/' + r.path] = r.meta.title as string
    }
  })

router.beforeEach((to, _from, next) => {
  const token = getToken()
  if (to.meta.noAuth) {
    if (token && to.path === '/login') {
      next('/dashboard')
    } else {
      next()
    }
  } else {
    if (!token) {
      next('/login')
    } else {
      next()
    }
  }
})

router.afterEach(async (to) => {
  if (to.name && to.name !== 'Login' && to.name !== 'Layout') {
    const { useTabsStore } = await import('@/stores/tabs')
    const tabsStore = useTabsStore()
    const title = (to.meta.title as string) || to.path
    const icon = to.meta.icon as string | undefined
    tabsStore.syncFromRoute(to.path, title, icon)
  }
})

export default router
