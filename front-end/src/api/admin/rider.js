// src/api/admin/rider.js
import request from '@/utils/request'

/**
 * 管理端：获取骑手列表（分页）
 * @param {Object} params 查询参数（pageNum, pageSize, keyword等）
 */
export function getRiderList(params) {
    return request({
        url: '/admin/riders',
        method: 'get',
        params
    })
}

/**
 * 管理端：新增骑手
 * @param {Object} data 骑手信息
 */
export function addRider(data) {
    return request({
        url: '/admin/riders',
        method: 'post',
        data
    })
}