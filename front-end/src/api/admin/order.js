// src/api/admin/order.js
import request from '@/utils/request'

/**
 * 管理端：获取订单列表（分页）
 * @param {Object} params 查询参数（pageNum, pageSize, status等）
 */
export function getOrderList(params) {
    return request({
        url: '/admin/orders',
        method: 'get',
        params
    })
}

/**
 * 管理端：获取订单详情
 * @param {number} orderId 订单ID
 */
export function getOrderDetail(orderId) {
    return request({
        url: `/admin/orders/${orderId}`,
        method: 'get'
    })
}