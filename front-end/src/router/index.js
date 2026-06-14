// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layouts/Layout.vue'

const routes = [
    {
        path: '/',
        component: Layout,
        redirect: '/admin/dashboard',
        children: [
            {
                path: '/admin/dashboard',
                component: () => import('@/views/admin/Dashboard.vue'),
                meta: { title: '仪表盘' }
            },
            {
                path: '/admin/user/list',
                component: () => import('@/views/admin/user/UserList.vue'),
                meta: { title: '用户管理' }
            },
            {
                path: '/admin/user/detail/:id',
                component: () => import('@/views/admin/user/UserDetail.vue'),
                meta: { title: '用户详情' }
            },
            {
                path: '/admin/merchant/list',
                component: () => import('@/views/admin/merchant/MerchantList.vue'),
                meta: { title: '商家管理' }
            },
            {
                path: '/admin/rider/list',
                component: () => import('@/views/admin/rider/RiderList.vue'),
                meta: { title: '骑手管理' }
            },
            {
                path: '/admin/shop/list',
                component: () => import('@/views/admin/shop/ShopList.vue'),
                meta: { title: '店铺管理' }
            },
            {
                path: '/admin/shop/detail/:id',
                component: () => import('@/views/admin/shop/ShopDetail.vue'),
                meta: { title: '店铺详情' }
            },
            {
                path: '/admin/order/list',
                component: () => import('@/views/admin/order/OrderList.vue'),
                meta: { title: '订单管理' }
            },
            {
                path: '/admin/order/detail/:id',
                component: () => import('@/views/admin/order/OrderDetail.vue'),
                meta: { title: '订单详情' }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router