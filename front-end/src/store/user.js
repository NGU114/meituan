// src/store/user.js
import { defineStore } from 'pinia'
import { getToken, setToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', {
    state: () => ({
        token: getToken(),
        userInfo: {}
    }),
    actions: {
        setToken(token) {
            this.token = token
            setToken(token)
        },
        logout() {
            this.token = ''
            this.userInfo = {}
            removeToken()
        }
    }
})