// src/api/admin/merchant.js
import request from '@/utils/request'

/**
 * 管理端：获取商家列表（分页）
 * @param {Object} params 查询参数（pageNum, pageSize, keyword等）
 */
export function getMerchantList(params) {
    return request({
        url: '/admin/merchants',
        method: 'get',
        params
    })
}

/**
 * 管理端：新增商家
 * @param {Object} data 商家信息
 */
export function addMerchant(data) {
    return request({
        url: '/admin/merchants',
        method: 'post',
        data
    })
}