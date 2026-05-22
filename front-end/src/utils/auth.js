// src/utils/auth.js
const TOKEN_KEY = 'ADMIN_TOKEN'

/**
 * 保存token到localStorage
 * @param {string} token
 */
export function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token)
}

/**
 * 从localStorage获取token
 * @returns {string|null}
 */
export function getToken() {
    return localStorage.getItem(TOKEN_KEY)
}

/**
 * 移除token
 */
export function removeToken() {
    localStorage.removeItem(TOKEN_KEY)
}