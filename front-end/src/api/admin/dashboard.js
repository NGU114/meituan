// src/api/admin/dashboard.js
import request from '@/utils/request'

/**
 * 管理端：获取仪表盘统计数据
 * 接口路径：/api/admin/dashboard
 */
export function getDashboard() {
    return request({
        url: '/admin/dashboard',
        method: 'get'
    })
}