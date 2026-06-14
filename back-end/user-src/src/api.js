const TOKEN_KEY = 'meituan_user_token'
const PROFILE_KEY = 'meituan_user_profile'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function getStoredProfile() {
  const raw = localStorage.getItem(PROFILE_KEY)
  return raw ? JSON.parse(raw) : null
}

export function saveSession(auth) {
  localStorage.setItem(TOKEN_KEY, auth.token)
  localStorage.setItem(PROFILE_KEY, JSON.stringify(auth))
}

export function clearSession() {
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

export const api = {
  login: (body) => request('/api/auth/login', { method: 'POST', body }),
  register: (body) => request('/api/auth/register', { method: 'POST', body }),
  me: () => request('/api/auth/me'),
  shops: () => request('/api/shops'),
  shopDetail: (shopId) => request(`/api/shops/${shopId}`),
  cart: () => request('/api/cart'),
  addCartItem: (body) => request('/api/cart/items', { method: 'POST', body }),
  updateCartItem: (cartItemId, quantity) => request(`/api/cart/items/${cartItemId}`, { method: 'PUT', body: { quantity } }),
  removeCartItem: (cartItemId) => request(`/api/cart/items/${cartItemId}`, { method: 'DELETE' }),
  clearCart: () => request('/api/cart', { method: 'DELETE' }),
  addresses: () => request('/api/addresses'),
  createAddress: (body) => request('/api/addresses', { method: 'POST', body }),
  deleteAddress: (addressId) => request(`/api/addresses/${addressId}`, { method: 'DELETE' }),
  orders: () => request('/api/orders'),
  orderDetail: (orderId) => request(`/api/orders/${orderId}`),
  createOrder: (body) => request('/api/orders', { method: 'POST', body }),
  payOrder: (orderId) => request(`/api/orders/${orderId}/pay`, { method: 'POST' }),
  cancelOrder: (orderId) => request(`/api/orders/${orderId}/cancel`, { method: 'POST' }),
  confirmOrder: (orderId) => request(`/api/orders/${orderId}/confirm`, { method: 'POST' }),
  commentOrder: (orderId, body) => request(`/api/orders/${orderId}/comment`, { method: 'POST', body })
}
