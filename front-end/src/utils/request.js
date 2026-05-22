// src/utils/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken } from './auth'

const request = axios.create({
    baseURL: 'http://localhost:8080/api', // 后端地址，根据实际情况修改
    timeout: 10000
})

// 请求拦截器：添加token到请求头
request.interceptors.request.use(
    config => {
        const token = getToken()
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    error => Promise.reject(error)
)

// 响应拦截器：统一处理响应
request.interceptors.response.use(
    res => {
        const { success, message, data } = res.data
        if (success) {
            return data
        } else {
            ElMessage.error(message || '请求失败')
            return Promise.reject(new Error(message))
        }
    },
    error => {
        ElMessage.error('网络异常或服务器错误')
        return Promise.reject(error)
    }
)

export default request