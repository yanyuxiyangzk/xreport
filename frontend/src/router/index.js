import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/report/list'
  },
  {
    path: '/report',
    name: 'Report',
    children: [
      {
        path: 'list',
        name: 'ReportList',
        component: () => import('@/views/report/List.vue'),
        meta: { title: '报表模板管理' }
      },
      {
        path: 'designer/:id?',
        name: 'ReportDesigner',
        component: () => import('@/views/report/Designer.vue'),
        meta: { title: '报表设计器' }
      },
      {
        path: 'preview/:id',
        name: 'ReportPreview',
        component: () => import('@/views/report/Preview.vue'),
        meta: { title: '报表预览' }
      }
    ]
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    children: [
      {
        path: 'list',
        name: 'DashboardList',
        component: () => import('@/views/dashboard/List.vue'),
        meta: { title: '大屏模板管理' }
      },
      {
        path: 'designer/:id?',
        name: 'DashboardDesigner',
        component: () => import('@/views/dashboard/Designer.vue'),
        meta: { title: '大屏设计器' }
      },
      {
        path: 'preview/:id',
        name: 'DashboardPreview',
        component: () => import('@/views/dashboard/Preview.vue'),
        meta: { title: '大屏预览' }
      }
    ]
  },
  {
    path: '/share',
    name: 'Share',
    children: [
      {
        path: 'list',
        name: 'ShareList',
        component: () => import('@/views/share/List.vue'),
        meta: { title: '分享管理' }
      },
      {
        path: ':shareToken',
        name: 'ShareView',
        component: () => import('@/views/share/View.vue'),
        meta: { title: '分享查看' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router