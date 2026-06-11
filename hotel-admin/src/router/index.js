import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '管理员登录' },
  },
  {
    path: '/',
    component: () => import('@/layout/AdminLayout.vue'),
    redirect: '/dashboard',
    meta: { requireAuth: true },
    children: [
      // ===== 仪表盘 =====
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '数据概览', icon: 'DataAnalysis' },
      },
      // ===== 酒店管理 =====
      {
        path: 'hotel/list',
        name: 'HotelList',
        component: () => import('@/views/hotel/List.vue'),
        meta: { title: '酒店管理', icon: 'OfficeBuilding' },
      },
      // ===== 房间管理 =====
      {
        path: 'room/category',
        name: 'RoomCategory',
        component: () => import('@/views/room/Category.vue'),
        meta: { title: '房间分类', icon: 'Menu' },
      },
      {
        path: 'room/type',
        name: 'RoomType',
        component: () => import('@/views/room/Type.vue'),
        meta: { title: '房型管理', icon: 'House' },
      },
      {
        path: 'room/guest-room',
        name: 'GuestRoom',
        component: () => import('@/views/room/GuestRoom.vue'),
        meta: { title: '客房管理', icon: 'Key' },
      },
      {
        path: 'room/price',
        name: 'RoomPrice',
        component: () => import('@/views/room/Price.vue'),
        meta: { title: '价格管理', icon: 'PriceTag' },
      },
      // ===== 订单管理 =====
      {
        path: 'order/list',
        name: 'OrderList',
        component: () => import('@/views/order/List.vue'),
        meta: { title: '订单管理', icon: 'Document' },
      },
      {
        path: 'order/checkin',
        name: 'Checkin',
        component: () => import('@/views/order/Checkin.vue'),
        meta: { title: '入住管理', icon: 'Checked' },
      },
      // ===== 评价管理 =====
      {
        path: 'review/list',
        name: 'ReviewList',
        component: () => import('@/views/review/List.vue'),
        meta: { title: '评价管理', icon: 'ChatDotRound' },
      },
      // ===== 用户管理 =====
      {
        path: 'user/list',
        name: 'UserList',
        component: () => import('@/views/user/List.vue'),
        meta: { title: '用户管理', icon: 'User' },
      },
      // ===== 系统管理 =====
      {
        path: 'system/admin',
        name: 'AdminManage',
        component: () => import('@/views/system/AdminManage.vue'),
        meta: { title: '管理员管理', icon: 'UserFilled' },
      },
      {
        path: 'system/role',
        name: 'RoleManage',
        component: () => import('@/views/system/RoleManage.vue'),
        meta: { title: '角色管理', icon: 'Lock' },
      },
      {
        path: 'system/resource',
        name: 'ResourceManage',
        component: () => import('@/views/system/ResourceManage.vue'),
        meta: { title: '资源库管理', icon: 'Files' },
      },
      {
        path: 'system/config',
        name: 'SystemConfig',
        component: () => import('@/views/system/Config.vue'),
        meta: { title: '系统配置', icon: 'Setting' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 酒店管理后台` : '酒店管理后台'

  if (to.meta.requireAuth || to.matched.some((r) => r.meta.requireAuth)) {
    const token = localStorage.getItem('admin_token')
    if (!token) {
      next({ path: '/login', query: { redirect: to.fullPath } })
      return
    }
  }
  next()
})

export default router
