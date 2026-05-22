// src/api/admin/user.js
import request from '@/utils/request'

/**
 * 管理端：获取用户列表（分页）
 * @param {Object} params 查询参数（pageNum, pageSize, keyword等）
 */
export function getUserList(params) {
    return request({
        url: '/admin/users',
        method: 'get',
        params
    })
}

/**
 * 管理端：获取用户详情
 * @param {number} userId 用户ID
 */
export function getUserDetail(userId) {
    return request({
        url: `/admin/users/${userId}`,
        method: 'get'
    })
}

/**
 * 管理端：修改用户状态
 * @param {number} userId 用户ID
 * @param {Object} data 状态信息（status）
 */
export function updateUserStatus(userId, data) {
    return request({
        url: `/admin/users/${userId}/status`,
        method: 'put',
        data
    })
}