// src/api/admin/shop.js
import request from '@/utils/request'

/**
 * 管理端：获取店铺列表（分页）
 * @param {Object} params 查询参数（pageNum, pageSize, status等）
 */
export function getShopList(params) {
    return request({
        url: '/admin/shops',
        method: 'get',
        params
    })
}

/**
 * 管理端：获取店铺详情
 * @param {number} shopId 店铺ID
 */
export function getShopDetail(shopId) {
    return request({
        url: `/admin/shops/${shopId}`,
        method: 'get'
    })
}

/**
 * 管理端：修改店铺营业状态
 * @param {number} shopId 店铺ID
 * @param {Object} data 状态信息（status）
 */
export function updateShopStatus(shopId, data) {
    return request({
        url: `/admin/shops/${shopId}/status`,
        method: 'put',
        data
    })
}