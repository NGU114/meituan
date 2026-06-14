const TOKEN_KEY = 'meituan_token'
const PROFILE_KEY = 'meituan_profile'

export function getToken() {
  return sessionStorage.getItem(TOKEN_KEY)
}

export function getStoredProfile() {
  const raw = sessionStorage.getItem(PROFILE_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    sessionStorage.removeItem(PROFILE_KEY)
    return null
  }
}

export function saveSession(auth) {
  sessionStorage.setItem(TOKEN_KEY, auth.token)
  sessionStorage.setItem(PROFILE_KEY, JSON.stringify(auth))
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(PROFILE_KEY)
}

export function clearSession() {
  sessionStorage.removeItem(TOKEN_KEY)
  sessionStorage.removeItem(PROFILE_KEY)
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(PROFILE_KEY)
}

async function request(path, options = {}) {
  const headers = {
    ...(options.body ? { 'Content-Type': 'application/json' } : {}),
    ...(options.headers || {})
  }
  const token = getToken()
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  const response = await fetch(path, {
    ...options,
    headers,
    body: options.body ? JSON.stringify(options.body) : undefined
  })

  const payload = await response.json().catch(() => null)
  if (!response.ok || payload?.success === false) {
    throw new Error(payload?.message || `请求失败：${response.status}`)
  }
  return payload?.data ?? null
}

const post = (path, body) => request(path, { method: 'POST', body })
const put = (path, body) => request(path, { method: 'PUT', body })
const del = (path) => request(path, { method: 'DELETE' })

export const api = {
  login: (body) => post('/api/auth/login', body),
  register: (body) => post('/api/auth/register', body),
  me: () => request('/api/auth/me'),

  shops: () => request('/api/shops'),
  shopDetail: (shopId) => request(`/api/shops/${shopId}`),

  cart: () => request('/api/cart'),
  addCartItem: (body) => post('/api/cart/items', body),
  updateCartItem: (cartItemId, quantity) => put(`/api/cart/items/${cartItemId}`, { quantity }),
  removeCartItem: (cartItemId) => del(`/api/cart/items/${cartItemId}`),
  clearCart: () => del('/api/cart'),

  addresses: () => request('/api/addresses'),
  createAddress: (body) => post('/api/addresses', body),
  updateAddress: (addressId, body) => put(`/api/addresses/${addressId}`, body),
  makeDefaultAddress: (addressId) => post(`/api/addresses/${addressId}/default`),
  deleteAddress: (addressId) => del(`/api/addresses/${addressId}`),

  orders: () => request('/api/orders'),
  orderDetail: (orderId) => request(`/api/orders/${orderId}`),
  createOrder: (body) => post('/api/orders', body),
  payOrder: (orderId) => post(`/api/orders/${orderId}/pay`),
  cancelOrder: (orderId) => post(`/api/orders/${orderId}/cancel`),
  requestRefund: (orderId) => post(`/api/orders/${orderId}/refund`),
  confirmOrder: (orderId) => post(`/api/orders/${orderId}/confirm`),
  commentOrder: (orderId, body) => post(`/api/orders/${orderId}/comment`, body),

  merchantShops: () => request('/api/merchant/shops'),
  updateMerchantShop: (shopId, body) => put(`/api/merchant/shops/${shopId}`, body),
  merchantCategories: () => request('/api/merchant/categories'),
  createMerchantCategory: (body) => post('/api/merchant/categories', body),
  updateMerchantCategory: (categoryId, body) => put(`/api/merchant/categories/${categoryId}`, body),
  deleteMerchantCategory: (categoryId) => del(`/api/merchant/categories/${categoryId}`),
  merchantProducts: () => request('/api/merchant/products'),
  createMerchantProduct: (body) => post('/api/merchant/products', body),
  updateMerchantProduct: (productId, body) => put(`/api/merchant/products/${productId}`, body),
  disableMerchantProduct: (productId) => del(`/api/merchant/products/${productId}`),
  merchantOrders: () => request('/api/merchant/orders'),
  merchantOrderDetail: (orderId) => request(`/api/merchant/orders/${orderId}`),
  merchantComments: () => request('/api/merchant/comments'),
  merchantAccept: (orderId) => post(`/api/merchant/orders/${orderId}/accept`),
  merchantReject: (orderId) => post(`/api/merchant/orders/${orderId}/reject`),
  merchantPreparing: (orderId) => post(`/api/merchant/orders/${orderId}/preparing`),
  merchantReady: (orderId) => post(`/api/merchant/orders/${orderId}/ready`),
  merchantApproveRefund: (orderId) => post(`/api/merchant/orders/${orderId}/refund/approve`),
  merchantRejectRefund: (orderId) => post(`/api/merchant/orders/${orderId}/refund/reject`),

  riderAvailable: () => request('/api/rider/orders/available'),
  riderMine: () => request('/api/rider/orders/mine'),
  riderOrderDetail: (orderId) => request(`/api/rider/orders/${orderId}`),
  riderTake: (orderId) => post(`/api/rider/orders/${orderId}/take`),
  riderDelivered: (orderId) => post(`/api/rider/orders/${orderId}/delivered`),

  adminDashboard: () => request('/api/admin/dashboard'),
  adminUsers: () => request('/api/admin/users'),
  updateAdminUserStatus: (userId, enabled) => put(`/api/admin/users/${userId}/status`, { enabled }),
  adminShops: () => request('/api/admin/shops'),
  updateAdminShopStatus: (shopId, open) => put(`/api/admin/shops/${shopId}/status`, { open }),
  adminOrders: () => request('/api/admin/orders'),
  adminOrderDetail: (orderId) => request(`/api/admin/orders/${orderId}`),
  deleteAdminOrder: (orderId) => del(`/api/admin/orders/${orderId}`)
}
