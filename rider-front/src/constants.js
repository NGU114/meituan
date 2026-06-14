export const roleNames = {
  USER: '用户',
  MERCHANT: '商家',
  RIDER: '骑手',
  ADMIN: '管理员'
}

export const demoAccounts = [
  { role: 'USER', label: '用户', username: 'user', password: '123456' },
  { role: 'MERCHANT', label: '商家', username: 'merchant', password: '123456' },
  { role: 'RIDER', label: '骑手', username: 'rider', password: '123456' },
  { role: 'ADMIN', label: '管理员', username: 'admin', password: '123456' }
]

export const guestTabs = [
  { key: 'shops', label: '点餐' }
]

export const tabSets = {
  USER: [
    { key: 'shops', label: '点餐' },
    { key: 'cart', label: '购物车' },
    { key: 'addresses', label: '地址' },
    { key: 'orders', label: '订单' }
  ],
  MERCHANT: [
    { key: 'merchant-dashboard', label: '经营概览' },
    { key: 'merchant-shop', label: '店铺' },
    { key: 'merchant-products', label: '菜品' },
    { key: 'merchant-orders', label: '订单' }
  ],
  RIDER: [
    { key: 'rider-available', label: '可接单' },
    { key: 'rider-mine', label: '我的配送' }
  ],
  ADMIN: [
    { key: 'admin-dashboard', label: '平台概览' },
    { key: 'admin-users', label: '用户' },
    { key: 'admin-shops', label: '店铺' },
    { key: 'admin-orders', label: '订单' }
  ]
}
