<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { api, clearSession, getStoredProfile, getToken, saveSession } from './api'

const tabs = [
  { key: 'shops', label: '点餐' },
  { key: 'cart', label: '购物车' },
  { key: 'addresses', label: '地址' },
  { key: 'orders', label: '订单' }
]

const view = ref('shops')
const authMode = ref('login')
const loading = ref(false)
const message = ref('')
const profile = ref(getStoredProfile())
const shops = ref([])
const activeShopId = ref(null)
const shopDetail = ref(null)
const cart = ref({ shopId: null, shopName: null, items: [], totalAmount: 0 })
const addresses = ref([])
const orders = ref([])
const activeOrder = ref(null)
const checkoutRemark = ref('')
const selectedAddressId = ref(null)

const loginForm = reactive({ username: 'user', password: '123456' })
const registerForm = reactive({ username: '', password: '', displayName: '', phone: '' })
const addressForm = reactive({ contactName: '', contactPhone: '', detailAddress: '', defaultAddress: false })
const commentForm = reactive({ rating: 5, content: '' })

const isAuthed = computed(() => Boolean(getToken() && profile.value))
const cartCount = computed(() => cart.value.items.reduce((sum, item) => sum + item.quantity, 0))
const cartShop = computed(() => shops.value.find((shop) => shop.id === cart.value.shopId) || null)
const checkoutPayAmount = computed(() => Number(cart.value.totalAmount || 0) + Number(cartShop.value?.deliveryFee || 0))
const cartProductIds = computed(() => new Set(cart.value.items.map((item) => item.productId)))
const canCheckout = computed(() => isAuthed.value && cart.value.items.length > 0 && selectedAddressId.value)

function notify(text) {
  message.value = text
  window.clearTimeout(notify.timer)
  notify.timer = window.setTimeout(() => {
    message.value = ''
  }, 2800)
}

async function run(action, successText) {
  loading.value = true
  try {
    const result = await action()
    if (successText) notify(successText)
    return result === null ? true : result
  } catch (error) {
    notify(error.message || '操作失败')
    return null
  } finally {
    loading.value = false
  }
}

function money(value) {
  return Number(value || 0).toFixed(2)
}

function shortTime(value) {
  if (!value) return ''
  return new Date(value).toLocaleString('zh-CN', { hour12: false })
}

function resetComment() {
  commentForm.rating = 5
  commentForm.content = ''
}

async function loadPublicData() {
  const data = await api.shops()
  shops.value = data
  if (!activeShopId.value && data.length > 0) {
    await selectShop(data[0].id)
  }
}

async function loadPrivateData() {
  if (!getToken()) return
  const [profileResult, cartResult, addressResult, orderResult] = await Promise.allSettled([
    api.me(),
    api.cart(),
    api.addresses(),
    api.orders()
  ])

  if (profileResult.status === 'fulfilled') {
    profile.value = { ...profile.value, ...profileResult.value }
  } else {
    clearSession()
    profile.value = null
    return
  }
  if (cartResult.status === 'fulfilled') cart.value = cartResult.value
  if (addressResult.status === 'fulfilled') {
    addresses.value = addressResult.value
    selectedAddressId.value = addressResult.value.find((item) => item.defaultAddress)?.id || addressResult.value[0]?.id || null
  }
  if (orderResult.status === 'fulfilled') orders.value = orderResult.value
}

async function selectShop(shopId) {
  activeShopId.value = shopId
  shopDetail.value = await run(() => api.shopDetail(shopId))
}

async function submitLogin() {
  const auth = await run(() => api.login(loginForm), '登录成功')
  if (!auth) return
  if (auth.role !== 'USER') {
    notify('请使用用户账号登录用户端')
    return
  }
  saveSession(auth)
  profile.value = auth
  await loadPrivateData()
}

async function submitRegister() {
  const auth = await run(() => api.register(registerForm), '注册成功')
  if (!auth) return
  saveSession(auth)
  profile.value = auth
  Object.assign(registerForm, { username: '', password: '', displayName: '', phone: '' })
  await loadPrivateData()
}

function logout() {
  clearSession()
  profile.value = null
  cart.value = { shopId: null, shopName: null, items: [], totalAmount: 0 }
  addresses.value = []
  orders.value = []
  activeOrder.value = null
  selectedAddressId.value = null
  notify('已退出登录')
}

async function addProduct(product) {
  if (!isAuthed.value) {
    authMode.value = 'login'
    notify('请先登录后点餐')
    return
  }
  const nextCart = await run(() => api.addCartItem({ productId: product.id, quantity: 1 }), '已加入购物车')
  if (nextCart) cart.value = nextCart
}

async function changeCartItem(item, delta) {
  const quantity = item.quantity + delta
  const nextCart = quantity <= 0
    ? await run(() => api.removeCartItem(item.id), '已移除商品')
    : await run(() => api.updateCartItem(item.id, quantity))
  if (nextCart) cart.value = nextCart
}

async function clearMyCart() {
  const ok = await run(() => api.clearCart(), '购物车已清空')
  if (ok) cart.value = { shopId: null, shopName: null, items: [], totalAmount: 0 }
}

async function createAddress() {
  const created = await run(() => api.createAddress(addressForm), '地址已保存')
  if (!created) return
  Object.assign(addressForm, { contactName: '', contactPhone: '', detailAddress: '', defaultAddress: false })
  addresses.value = await api.addresses()
  selectedAddressId.value = created.id
}

async function removeAddress(addressId) {
  const ok = await run(() => api.deleteAddress(addressId), '地址已删除')
  if (!ok) return
  addresses.value = await api.addresses()
  selectedAddressId.value = addresses.value.find((item) => item.defaultAddress)?.id || addresses.value[0]?.id || null
}

async function createOrder() {
  if (!canCheckout.value) {
    notify('请选择收货地址')
    return
  }
  const order = await run(
    () => api.createOrder({ addressId: selectedAddressId.value, remark: checkoutRemark.value }),
    '下单成功'
  )
  if (!order) return
  checkoutRemark.value = ''
  activeOrder.value = order
  view.value = 'orders'
  cart.value = await api.cart()
  orders.value = await api.orders()
}

async function refreshOrder(orderId) {
  activeOrder.value = await run(() => api.orderDetail(orderId))
}

async function payOrder(orderId) {
  const order = await run(() => api.payOrder(orderId), '支付成功')
  if (!order) return
  activeOrder.value = order
  orders.value = await api.orders()
}

async function cancelOrder(orderId) {
  const order = await run(() => api.cancelOrder(orderId), '订单已取消')
  if (!order) return
  activeOrder.value = order
  orders.value = await api.orders()
  if (activeShopId.value) await selectShop(activeShopId.value)
}

async function confirmOrder(orderId) {
  const order = await run(() => api.confirmOrder(orderId), '确认收货成功')
  if (!order) return
  activeOrder.value = order
  orders.value = await api.orders()
}

async function submitComment(orderId) {
  const comment = await run(() => api.commentOrder(orderId, commentForm), '评价成功')
  if (!comment) return
  resetComment()
  await refreshOrder(orderId)
  orders.value = await api.orders()
}

onMounted(async () => {
  await run(async () => {
    await loadPublicData()
    await loadPrivateData()
  })
})
</script>

<template>
  <div class="app-shell">
    <header class="topbar">
      <div>
        <p class="eyebrow">用户端</p>
        <h1>美团外卖</h1>
      </div>
      <nav class="tabs">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          :class="{ active: view === tab.key }"
          type="button"
          @click="view = tab.key"
        >
          {{ tab.label }}
          <span v-if="tab.key === 'cart' && cartCount" class="badge">{{ cartCount }}</span>
        </button>
      </nav>
      <div class="account">
        <template v-if="isAuthed">
          <span>{{ profile.displayName || profile.username }}</span>
          <button type="button" class="ghost" @click="logout">退出</button>
        </template>
        <template v-else>
          <button type="button" class="ghost" @click="authMode = 'login'">登录</button>
          <button type="button" @click="authMode = 'register'">注册</button>
        </template>
      </div>
    </header>

    <p v-if="message" class="toast">{{ message }}</p>

    <main class="layout">
      <aside class="auth-panel">
        <section v-if="!isAuthed" class="panel">
          <div class="segmented">
            <button type="button" :class="{ active: authMode === 'login' }" @click="authMode = 'login'">登录</button>
            <button type="button" :class="{ active: authMode === 'register' }" @click="authMode = 'register'">注册</button>
          </div>

          <form v-if="authMode === 'login'" class="form" @submit.prevent="submitLogin">
            <label>用户名<input v-model.trim="loginForm.username" required /></label>
            <label>密码<input v-model="loginForm.password" type="password" required /></label>
            <button type="submit" :disabled="loading">登录</button>
          </form>

          <form v-else class="form" @submit.prevent="submitRegister">
            <label>用户名<input v-model.trim="registerForm.username" minlength="4" maxlength="20" required /></label>
            <label>密码<input v-model="registerForm.password" type="password" minlength="6" maxlength="20" required /></label>
            <label>昵称<input v-model.trim="registerForm.displayName" maxlength="32" required /></label>
            <label>手机号<input v-model.trim="registerForm.phone" pattern="^1\d{10}$" required /></label>
            <button type="submit" :disabled="loading">创建用户账号</button>
          </form>
        </section>

        <section v-else class="panel profile-card">
          <p class="eyebrow">当前用户</p>
          <h2>{{ profile.displayName }}</h2>
          <p>{{ profile.phone || profile.username }}</p>
          <button type="button" @click="view = 'orders'">查看我的订单</button>
        </section>

        <section class="panel compact-cart">
          <div class="section-head">
            <h2>购物车</h2>
            <button v-if="cart.items.length" type="button" class="ghost" @click="clearMyCart">清空</button>
          </div>
          <p v-if="!cart.items.length" class="muted">还没有添加商品</p>
          <div v-else class="cart-list">
            <p class="cart-shop">{{ cart.shopName }}</p>
            <div v-for="item in cart.items" :key="item.id" class="cart-line">
              <div>
                <strong>{{ item.productName }}</strong>
                <span>¥{{ money(item.price) }}</span>
              </div>
              <div class="stepper">
                <button type="button" @click="changeCartItem(item, -1)">-</button>
                <span>{{ item.quantity }}</span>
                <button type="button" @click="changeCartItem(item, 1)">+</button>
              </div>
            </div>
            <div class="total-line">
              <span>商品小计</span>
              <strong>¥{{ money(cart.totalAmount) }}</strong>
            </div>
            <button type="button" @click="view = 'cart'">去结算</button>
          </div>
        </section>
      </aside>

      <section v-if="view === 'shops'" class="content-grid">
        <div class="shop-list">
          <button
            v-for="shop in shops"
            :key="shop.id"
            type="button"
            class="shop-row"
            :class="{ active: shop.id === activeShopId }"
            @click="selectShop(shop.id)"
          >
            <span>{{ shop.name }}</span>
            <small>起送 ¥{{ money(shop.minOrderAmount) }} · 配送 ¥{{ money(shop.deliveryFee) }}</small>
          </button>
        </div>

        <article v-if="shopDetail" class="shop-detail">
          <div class="hero">
            <div>
              <p class="eyebrow">商家</p>
              <h2>{{ shopDetail.name }}</h2>
              <p>{{ shopDetail.announcement }}</p>
            </div>
            <div class="hero-price">
              <span>起送 ¥{{ money(shopDetail.minOrderAmount) }}</span>
              <span>配送 ¥{{ money(shopDetail.deliveryFee) }}</span>
            </div>
          </div>

          <section v-for="category in shopDetail.categories" :key="category.id" class="menu-section">
            <h3>{{ category.name }}</h3>
            <div class="product-grid">
              <article v-for="product in category.products" :key="product.id" class="product-card">
                <div>
                  <h4>{{ product.name }}</h4>
                  <p>{{ product.description }}</p>
                  <small>库存 {{ product.stock }}</small>
                </div>
                <div class="product-action">
                  <strong>¥{{ money(product.price) }}</strong>
                  <button type="button" :disabled="product.stock < 1 || loading" @click="addProduct(product)">
                    {{ cartProductIds.has(product.id) ? '再来一份' : '加入' }}
                  </button>
                </div>
              </article>
            </div>
          </section>
        </article>
      </section>

      <section v-if="view === 'cart'" class="content-stack">
        <div class="panel">
          <div class="section-head">
            <h2>结算</h2>
            <span v-if="cart.shopName">{{ cart.shopName }}</span>
          </div>
          <p v-if="!cart.items.length" class="muted">购物车为空，请先选择商品。</p>
          <template v-else>
            <div v-for="item in cart.items" :key="item.id" class="checkout-line">
              <span>{{ item.productName }} x {{ item.quantity }}</span>
              <strong>¥{{ money(item.amount) }}</strong>
            </div>
            <div class="total-line large">
              <span>需支付</span>
              <strong>¥{{ money(checkoutPayAmount) }}</strong>
            </div>
          </template>
        </div>

        <div class="panel">
          <div class="section-head">
            <h2>收货地址</h2>
            <button type="button" class="ghost" @click="view = 'addresses'">管理地址</button>
          </div>
          <div v-if="addresses.length" class="address-options">
            <label v-for="address in addresses" :key="address.id" class="address-option">
              <input v-model="selectedAddressId" type="radio" :value="address.id" />
              <span>
                <strong>{{ address.contactName }} {{ address.contactPhone }}</strong>
                {{ address.detailAddress }}
              </span>
            </label>
          </div>
          <p v-else class="muted">请先添加收货地址。</p>
          <label class="remark">备注<textarea v-model.trim="checkoutRemark" maxlength="255" rows="3" /></label>
          <button type="button" :disabled="!canCheckout || loading" @click="createOrder">提交订单</button>
        </div>
      </section>

      <section v-if="view === 'addresses'" class="content-stack">
        <div class="panel">
          <h2>新增地址</h2>
          <form class="address-form" @submit.prevent="createAddress">
            <label>联系人<input v-model.trim="addressForm.contactName" maxlength="32" required /></label>
            <label>联系电话<input v-model.trim="addressForm.contactPhone" pattern="^1\d{10}$" required /></label>
            <label class="wide">详细地址<input v-model.trim="addressForm.detailAddress" maxlength="255" required /></label>
            <label class="check"><input v-model="addressForm.defaultAddress" type="checkbox" />设为默认地址</label>
            <button type="submit" :disabled="loading">保存地址</button>
          </form>
        </div>

        <div class="panel">
          <h2>我的地址</h2>
          <p v-if="!addresses.length" class="muted">暂无地址。</p>
          <div v-for="address in addresses" :key="address.id" class="address-card">
            <div>
              <strong>{{ address.contactName }} {{ address.contactPhone }}</strong>
              <p>{{ address.detailAddress }}</p>
              <span v-if="address.defaultAddress" class="tag">默认</span>
            </div>
            <button type="button" class="danger" @click="removeAddress(address.id)">删除</button>
          </div>
        </div>
      </section>

      <section v-if="view === 'orders'" class="orders-layout">
        <div class="panel order-list">
          <h2>我的订单</h2>
          <p v-if="!orders.length" class="muted">暂无订单。</p>
          <button
            v-for="order in orders"
            :key="order.id"
            type="button"
            class="order-row"
            :class="{ active: activeOrder?.id === order.id }"
            @click="refreshOrder(order.id)"
          >
            <span>
              <strong>{{ order.shopName }}</strong>
              <small>{{ shortTime(order.createdAt) }}</small>
            </span>
            <span>
              <b>{{ order.statusLabel }}</b>
              <strong>¥{{ money(order.payAmount) }}</strong>
            </span>
          </button>
        </div>

        <div class="panel order-detail">
          <template v-if="activeOrder">
            <div class="section-head">
              <div>
                <p class="eyebrow">订单 {{ activeOrder.orderNo }}</p>
                <h2>{{ activeOrder.shopName }}</h2>
              </div>
              <span class="status">{{ activeOrder.statusLabel }}</span>
            </div>
            <div class="info-grid">
              <p><span>联系人</span>{{ activeOrder.contactName }} {{ activeOrder.contactPhone }}</p>
              <p><span>地址</span>{{ activeOrder.addressSnapshot }}</p>
              <p><span>骑手</span>{{ activeOrder.riderName || '待分配' }}</p>
              <p><span>备注</span>{{ activeOrder.remark || '无' }}</p>
            </div>
            <div v-for="item in activeOrder.items" :key="item.productName" class="checkout-line">
              <span>{{ item.productName }} x {{ item.quantity }}</span>
              <strong>¥{{ money(item.amount) }}</strong>
            </div>
            <div class="price-lines">
              <p><span>商品</span><strong>¥{{ money(activeOrder.goodsAmount) }}</strong></p>
              <p><span>配送费</span><strong>¥{{ money(activeOrder.deliveryFee) }}</strong></p>
              <p class="total"><span>合计</span><strong>¥{{ money(activeOrder.payAmount) }}</strong></p>
            </div>
            <div class="action-row">
              <button v-if="activeOrder.status === 'PENDING_PAYMENT'" type="button" @click="payOrder(activeOrder.id)">模拟支付</button>
              <button v-if="['PENDING_PAYMENT', 'PAID'].includes(activeOrder.status)" type="button" class="danger" @click="cancelOrder(activeOrder.id)">取消订单</button>
              <button v-if="activeOrder.status === 'DELIVERING'" type="button" @click="confirmOrder(activeOrder.id)">确认收货</button>
            </div>
            <form v-if="activeOrder.status === 'COMPLETED' && !activeOrder.commented" class="comment-form" @submit.prevent="submitComment(activeOrder.id)">
              <label>评分<input v-model.number="commentForm.rating" type="number" min="1" max="5" required /></label>
              <label>评价<textarea v-model.trim="commentForm.content" maxlength="255" rows="3" required /></label>
              <button type="submit">提交评价</button>
            </form>
            <div v-if="activeOrder.comment" class="comment-view">
              <strong>{{ activeOrder.comment.rating }} 分评价</strong>
              <p>{{ activeOrder.comment.content }}</p>
            </div>
          </template>
          <p v-else class="muted">选择左侧订单查看详情。</p>
        </div>
      </section>
    </main>
  </div>
</template>
